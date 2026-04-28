package com.example.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.FileInfo;
import com.example.service.FileInfoService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/front/v2")
public class TutorialPlatformController {

    /** 模拟物流：卖家发货后延迟将订单从「运输中」置为「待取货」 */
    private static final ScheduledExecutorService TP_ORDER_SCHEDULER = Executors.newScheduledThreadPool(2);

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 教程平台前台图片上传（走 /front/v2 免登录拦截器），返回可存入教程/头像的相对路径
     */
    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadTutorialImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "请选择图片文件");
        }
        String originName = file.getOriginalFilename();
        if (originName == null) {
            return Result.error("1001", "文件名不能为空");
        }
        String lower = originName.toLowerCase(Locale.ROOT);
        if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif"))) {
            return Result.error("1001", "仅支持 png、jpg、jpeg、gif");
        }
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        String fileName = FileUtil.mainName(originName) + System.currentTimeMillis() + "." + FileUtil.extName(originName);
        FileUtil.writeBytes(file.getBytes(), basePath + fileName);
        FileInfo info = new FileInfo();
        info.setOriginName(originName);
        info.setFileName(fileName);
        FileInfo saved = fileInfoService.add(info);
        if (saved == null) {
            return Result.error("4001", "上传失败");
        }
        Map<String, String> data = new HashMap<String, String>(2);
        data.put("url", "/files/download/" + saved.getId());
        return Result.success(data);
    }

    // ========================
    // 公共功能
    // ========================
/**
 * 获取首页教程列表接口
 * @param sort 排序方式，默认为"hot"（热门），可选"new"（最新）
 * @return 返回包含教程列表的Result对象，教程信息包括：
 *         - id: 教程ID
 *         - title: 教程标题
 *         - cover_image_url: 封面图片URL
 *         - summary: 教程简介
 *         - difficulty_level: 难度等级
 *         - production_time_minutes: 制作时长（分钟）
 *         - material_summary: 材料总结
 *         - view_count: 浏览量
 *         - like_count: 点赞数
 *         - favorite_count: 收藏数
 *         - comment_count: 评论数
 *         - author_name: 作者昵称
 */
    @GetMapping("/home")
    public Result home(@RequestParam(defaultValue = "hot") String sort) {
    // 设置默认排序条件为按热度评分降序、ID降序排列
        String orderBy = "t.hot_score desc,t.id desc";
    // 如果是最新排序，则只按ID降序排列
        if ("new".equalsIgnoreCase(sort)) {
            orderBy = "t.id desc";
        }
    // 查询教程列表，关联获取作者昵称，只查询已发布的教程(status=2)
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select t.id,t.title,t.cover_image_url,t.summary,t.difficulty_level,t.production_time_minutes," +
                        "t.material_summary,t.view_count,t.like_count,t.favorite_count,t.comment_count,u.nickname as author_name " +
                        "from tp_tutorial t left join tp_user u on t.author_user_id=u.id where t.status=2 order by " + orderBy + " limit 12");
    // 返回成功结果和数据
        return Result.success(list);
    }

    @GetMapping("/tutorial/filter-meta")
    public Result tutorialFilterMeta() {
        List<Map<String, Object>> materials = jdbcTemplate.queryForList("select id,material_name from tp_material_dict where status=1 order by id asc");
        List<Map<String, Object>> tags = jdbcTemplate.queryForList("select id,tag_name from tp_tutorial_tag where status=1 order by sort_no asc,id asc");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("materials", materials);
        result.put("tags", tags);
        result.put("difficultyLevels", Arrays.asList(
                mapOf("value", 1, "label", "入门"),
                mapOf("value", 2, "label", "普通"),
                mapOf("value", 3, "label", "进阶"),
                mapOf("value", 4, "label", "困难")
        ));
        result.put("timeRanges", Arrays.asList(
                mapOf("value", "0-30", "label", "30分钟内"),
                mapOf("value", "31-60", "label", "31-60分钟"),
                mapOf("value", "61-120", "label", "1-2小时"),
                mapOf("value", "121-9999", "label", "2小时以上")
        ));
        return Result.success(result);
    }

    @PostMapping("/tutorial/page")
    public Result tutorialPage(@RequestBody Map<String, Object> body) {
        int current = Math.max(1, toInt(body.get("current"), 1));
        int size = Math.max(1, toInt(body.get("pageSize"), 10));
        String keyword = asString(body.get("keyword"));
        Integer difficulty = body.get("difficultyLevel") == null ? null : toInt(body.get("difficultyLevel"), 0);
        Integer materialId = body.get("materialId") == null ? null : toInt(body.get("materialId"), 0);
        Integer tagId = body.get("tagId") == null ? null : toInt(body.get("tagId"), 0);
        String timeRange = asString(body.get("timeRange"));

        StringBuilder where = new StringBuilder(" from tp_tutorial t left join tp_user u on t.author_user_id=u.id where t.status=2 ");
        List<Object> params = new ArrayList<Object>();

        if (StrUtil.isNotBlank(keyword)) {
            where.append(" and (t.title like ? or t.material_summary like ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (difficulty != null && difficulty > 0) {
            where.append(" and t.difficulty_level=? ");
            params.add(difficulty);
        }
        if (StrUtil.isNotBlank(timeRange) && timeRange.contains("-")) {
            String[] arr = timeRange.split("-");
            if (arr.length == 2) {
                where.append(" and t.production_time_minutes between ? and ? ");
                params.add(toInt(arr[0], 0));
                params.add(toInt(arr[1], 9999));
            }
        }
        if (materialId != null && materialId > 0) {
            where.append(" and exists(select 1 from tp_tutorial_material_rel r where r.tutorial_id=t.id and r.material_id=?) ");
            params.add(materialId);
        }
        if (tagId != null && tagId > 0) {
            where.append(" and exists(select 1 from tp_tutorial_tag_rel tr where tr.tutorial_id=t.id and tr.tag_id=?) ");
            params.add(tagId);
        }

        String sort = asString(body.get("sort"));
        String orderBy = " order by t.hot_score desc,t.id desc ";
        if ("new".equalsIgnoreCase(sort)) {
            orderBy = " order by t.id desc ";
        }

        Integer total = jdbcTemplate.queryForObject("select count(1) " + where.toString(), Integer.class, params.toArray());
        int offset = (current - 1) * size;
        List<Object> pageParams = new ArrayList<Object>(params);
        pageParams.add(offset);
        pageParams.add(size);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select t.id,t.title,t.cover_image_url,t.summary,t.difficulty_level,t.production_time_minutes," +
                        "t.material_summary,t.view_count,t.like_count,t.favorite_count,t.comment_count,u.nickname as author_name " +
                        where.toString() + orderBy + " limit ?,?", pageParams.toArray());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("current", current);
        result.put("pageSize", size);
        result.put("total", total == null ? 0 : total);
        result.put("list", list);
        return Result.success(result);
    }

/**
 * 处理搜索请求的GET接口
 * @param keyword 搜索关键词
 * @param current 当前页码，默认值为1
 * @param pageSize 每页大小，默认值为10
 * @return 返回搜索结果，封装在Result对象中
 */
    @GetMapping("/search")    // HTTP GET请求映射到/search路径
    public Result search(@RequestParam String keyword,    // 请求参数：搜索关键词
                         @RequestParam(defaultValue = "1") Integer current,    // 请求参数：当前页，默认值为1
                         @RequestParam(defaultValue = "10") Integer pageSize) {    // 请求参数：每页大小，默认值为10
        Map<String, Object> body = new HashMap<String, Object>();    // 创建一个HashMap对象，用于存储请求参数
        body.put("keyword", keyword);    // 将搜索关键词添加到请求体中
        body.put("current", current);    // 将当前页码添加到请求体中
        body.put("pageSize", pageSize);    // 将每页大小添加到请求体中
        body.put("sort", "hot");    // 设置排序方式为"hot"（热门）
        return tutorialPage(body);    // 调用tutorialPage方法处理分页查询并返回结果
    }

/**
 * 获取教程详情的接口方法
 * 根据用户登录状态和教程ID返回不同的内容
 * @param id 教程ID
 * @param userId 用户ID（可选，未登录时为null）
 * @return 返回包含教程详情、步骤、材料包、标签、材料、评论等信息的Result对象
 */
    @GetMapping("/tutorial/{id}")
    public Result tutorialDetail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        // 公开仅可看 status=2；登录作者可看本人 1私密/2公开/3禁止公开（5已删不可见）
        List<Map<String, Object>> tutorials;
        if (userId == null) {
        // 未登录用户只能查看公开状态(status=2)的教程
            tutorials = jdbcTemplate.queryForList(
                    "select t.*,u.nickname as author_name from tp_tutorial t left join tp_user u on t.author_user_id=u.id where t.id=? and t.status=2", id);
        } else {
        // 已登录用户可查看本人所有教程（除已删除外）和公开教程
            tutorials = jdbcTemplate.queryForList(
                    "select t.*,u.nickname as author_name from tp_tutorial t left join tp_user u on t.author_user_id=u.id where t.id=? and t.status<>5 and (t.status=2 or t.author_user_id=?)",
                    id, userId);
        }
        if (tutorials.isEmpty()) {
            return Result.error("404", "教程不存在");
        }
    // 更新教程的浏览次数和热度分数
        jdbcTemplate.update("update tp_tutorial set view_count=view_count+1,hot_score=hot_score+0.2 where id=?", id);
        Map<String, Object> tutorial = tutorials.get(0);
    // 获取教程步骤信息
        List<Map<String, Object>> steps = jdbcTemplate.queryForList(
                "select id,step_no,step_title,step_content,step_image_url from tp_tutorial_step where tutorial_id=? order by step_no asc", id);
    // 获取教程材料包信息
        List<Map<String, Object>> kit = jdbcTemplate.queryForList(
                "select id,kit_name,kit_desc,price,stock,sales_count,status from tp_material_kit where tutorial_id=? and status in (1,2,3)", id);
    // 获取教程标签信息
        List<Map<String, Object>> tags = jdbcTemplate.queryForList(
                "select tg.id,tg.tag_name from tp_tutorial_tag_rel tr left join tp_tutorial_tag tg on tr.tag_id=tg.id where tr.tutorial_id=?", id);
    // 获取教程材料信息
        List<Map<String, Object>> materials = jdbcTemplate.queryForList(
                "select m.id,m.material_name from tp_tutorial_material_rel r left join tp_material_dict m on r.material_id=m.id where r.tutorial_id=?", id);
    // 获取教程评论信息
        List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                "select c.id,c.user_id,c.content,c.parent_comment_id,c.created_at,u.nickname as user_name " +
                        "from tp_tutorial_comment c left join tp_user u on c.user_id=u.id where c.tutorial_id=? and c.status=1 order by c.id desc", id);
    // 构建返回结果
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("tutorial", tutorial);
        result.put("steps", steps);
        result.put("materialKit", kit.isEmpty() ? null : kit.get(0));
        result.put("tags", tags);
        result.put("materials", materials);
        result.put("comments", comments);
    // 如果用户已登录，查询点赞和收藏状态
        if (userId != null) {
            Integer liked = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_like where tutorial_id=? and user_id=?", Integer.class, id, userId);
            Integer fav = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_favorite where tutorial_id=? and user_id=?", Integer.class, id, userId);
            result.put("userLiked", liked != null && liked > 0);
            result.put("userFavorited", fav != null && fav > 0);
        } else {
            result.put("userLiked", false);
            result.put("userFavorited", false);
        }
        return Result.success(result);
    }

    @GetMapping("/community/page")
    public Result communityPage(@RequestParam(defaultValue = "1") Integer current,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(defaultValue = "hot") String sort) {
        int page = Math.max(1, current == null ? 1 : current);
        int size = Math.max(1, pageSize == null ? 10 : pageSize);
        String orderBy = "p.hot_score desc,p.id desc";
        if ("new".equalsIgnoreCase(sort)) {
            orderBy = "p.id desc";
        }
        Integer total = jdbcTemplate.queryForObject("select count(1) from tp_community_post where status=1", Integer.class);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select p.id,p.author_user_id,p.title,left(p.content,120) as preview,p.comment_count,p.view_count,p.created_at,u.nickname as author_name " +
                        "from tp_community_post p left join tp_user u on p.author_user_id=u.id where p.status=1 order by " + orderBy + " limit ?,?",
                (page - 1) * size, size);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("current", page);
        result.put("pageSize", size);
        result.put("total", total == null ? 0 : total);
        result.put("list", list);
        return Result.success(result);
    }

    @GetMapping("/community/{id}")
    public Result postDetail(@PathVariable Long id) {
        List<Map<String, Object>> posts = jdbcTemplate.queryForList(
                "select p.id,p.author_user_id,p.title,p.content,p.comment_count,p.view_count,p.created_at,u.nickname as author_name " +
                        "from tp_community_post p left join tp_user u on p.author_user_id=u.id where p.id=? and p.status=1", id);
        if (posts.isEmpty()) {
            return Result.error("404", "帖子不存在");
        }
        jdbcTemplate.update("update tp_community_post set view_count=view_count+1,hot_score=hot_score+0.2 where id=?", id);
        List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                "select c.id,c.user_id,c.content,c.parent_comment_id,c.created_at,u.nickname as user_name " +
                        "from tp_community_post_comment c left join tp_user u on c.user_id=u.id where c.post_id=? and c.status=1 order by c.id asc", id);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("post", posts.get(0));
        result.put("comments", comments);
        return Result.success(result);
    }

    @GetMapping("/static/{pageKey}")
    public Result staticPage(@PathVariable String pageKey) {
        List<Map<String, Object>> pages = jdbcTemplate.queryForList(
                "select page_key,title,content from tp_cms_page where page_key=? and status=1", pageKey);
        if (pages.isEmpty()) {
            return Result.error("404", "页面不存在");
        }
        return Result.success(pages.get(0));
    }

    // ========================
    // 账号与用户功能
    // ========================
    @PostMapping("/auth/register")
    public Result register(@RequestBody Map<String, Object> body) {
        String username = asString(body.get("username"));
        String password = asString(body.get("password"));
        String nickname = asString(body.get("nickname"));
        if (StrUtil.hasBlank(username, password, nickname)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "username/password/nickname 不能为空");
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_user where username=?", Integer.class, username);
        if (count != null && count > 0) {
            return Result.error(ResultCode.USER_EXIST_ERROR.code, "用户名已存在");
        }
        jdbcTemplate.update("insert into tp_user(username,password_hash,nickname,role_type,status,wallet_balance,total_earnings) values(?,?,?,?,1,1000.00,0.00)",
                username, SecureUtil.md5(password), nickname, 1);
        return Result.success();
    }

    @PostMapping("/auth/login")
    public Result login(@RequestBody Map<String, Object> body) {
        String username = asString(body.get("username"));
        String password = asString(body.get("password"));
        if (StrUtil.hasBlank(username, password)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "username/password 不能为空");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,username,nickname,avatar_url,phone,wallet_balance,total_earnings,role_type,status from tp_user where username=? and password_hash=? limit 1",
                username, SecureUtil.md5(password));
        if (list.isEmpty()) {
            return Result.error(ResultCode.USER_ACCOUNT_ERROR.code, "账号或密码错误");
        }
        Map<String, Object> user = list.get(0);
        if (toInt(user.get("status"), 0) == 0) {
            return Result.error("2006", "账号已被禁用");
        }
        return Result.success(user);
    }

    @GetMapping("/user/{userId}")
    public Result userProfile(@PathVariable Long userId) {
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "select id,username,nickname,avatar_url,phone,wallet_balance,total_earnings,role_type,status from tp_user where id=?", userId);
        if (users.isEmpty()) {
            return Result.error("404", "用户不存在");
        }
        return Result.success(users.get(0));
    }

    @PutMapping("/user/profile")
    public Result updateUserProfile(@RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (userId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId 不能为空");
        }
        String nickname = asString(body.get("nickname"));
        String avatarUrl = asString(body.get("avatarUrl"));
        String phone = asString(body.get("phone"));
        // 昵称空则不修改；头像、手机允许置空（与前端「恢复默认头像」一致）
        jdbcTemplate.update("update tp_user set nickname=ifnull(?,nickname),avatar_url=?,phone=? where id=?",
                emptyToNull(nickname), emptyToNull(avatarUrl), emptyToNull(phone), userId);
        return Result.success();
    }

    /**
     * 模拟充值：直接设置钱包余额（用于测试）
     */
    @PutMapping("/user/wallet")
    public Result updateWalletSimulate(@RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        BigDecimal newBalance = toDecimal(body.get("balance"), null);
        if (userId == null || newBalance == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/balance 不能为空");
        }
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return Result.error(ResultCode.PARAM_ERROR.code, "余额不能为负数");
        }
        int n = jdbcTemplate.update("update tp_user set wallet_balance=? where id=?", newBalance, userId);
        if (n == 0) {
            return Result.error("404", "用户不存在");
        }
        return Result.success();
    }

    @PutMapping("/user/password")
    public Result updateUserPassword(@RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        String oldPassword = asString(body.get("oldPassword"));
        String newPassword = asString(body.get("newPassword"));
        if (userId == null || StrUtil.hasBlank(oldPassword, newPassword)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/oldPassword/newPassword 不能为空");
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_user where id=? and password_hash=?", Integer.class,
                userId, SecureUtil.md5(oldPassword));
        if (count == null || count == 0) {
            return Result.error(ResultCode.PARAM_PASSWORD_ERROR.code, "原密码错误");
        }
        jdbcTemplate.update("update tp_user set password_hash=? where id=?", SecureUtil.md5(newPassword), userId);
        return Result.success();
    }

    @PostMapping("/tutorial/publish")
    public Result publishTutorial(@RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        String title = asString(body.get("title"));
        if (userId == null || StrUtil.isBlank(title)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/title 不能为空");
        }
        String coverImageUrl = asString(body.get("coverImageUrl"));
        String summary = asString(body.get("summary"));
        int difficultyLevel = toInt(body.get("difficultyLevel"), 2);
        Integer productionTimeMinutes = body.get("productionTimeMinutes") == null ? null : toInt(body.get("productionTimeMinutes"), 0);
        String materialSummary = asString(body.get("materialSummary"));

        jdbcTemplate.update("insert into tp_tutorial(author_user_id,title,cover_image_url,summary,difficulty_level,production_time_minutes,material_summary,status) values(?,?,?,?,?,?,?,2)",
                userId, title, emptyToNull(coverImageUrl), emptyToNull(summary), difficultyLevel, productionTimeMinutes, emptyToNull(materialSummary));
        Long tutorialId = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        saveStepsAndRelations(tutorialId, body);
        saveOrUpdateKit(tutorialId, userId, body, false);
        return Result.success(tutorialId);
    }

    @PutMapping("/tutorial/{id}")
    public Result updateTutorial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (!isTutorialOwner(id, userId)) {
            return Result.error("403", "无权限编辑该教程");
        }
        jdbcTemplate.update("update tp_tutorial set title=ifnull(?,title),cover_image_url=ifnull(?,cover_image_url),summary=ifnull(?,summary)," +
                        "difficulty_level=ifnull(?,difficulty_level),production_time_minutes=ifnull(?,production_time_minutes),material_summary=ifnull(?,material_summary) where id=?",
                emptyToNull(asString(body.get("title"))),
                emptyToNull(asString(body.get("coverImageUrl"))),
                emptyToNull(asString(body.get("summary"))),
                body.get("difficultyLevel") == null ? null : toInt(body.get("difficultyLevel"), 2),
                body.get("productionTimeMinutes") == null ? null : toInt(body.get("productionTimeMinutes"), 0),
                emptyToNull(asString(body.get("materialSummary"))),
                id);

        jdbcTemplate.update("delete from tp_tutorial_step where tutorial_id=?", id);
        jdbcTemplate.update("delete from tp_tutorial_material_rel where tutorial_id=?", id);
        jdbcTemplate.update("delete from tp_tutorial_tag_rel where tutorial_id=?", id);
        saveStepsAndRelations(id, body);
        saveOrUpdateKit(id, userId, body, true);
        return Result.success();
    }

    @DeleteMapping("/tutorial/{id}")
    public Result deleteTutorial(@PathVariable Long id, @RequestParam Long userId) {
        if (!isTutorialOwner(id, userId)) {
            return Result.error("403", "无权限删除该教程");
        }
        jdbcTemplate.update("update tp_tutorial set status=5 where id=?", id);
        return Result.success();
    }

    @GetMapping("/user/tutorials")
    public Result myTutorials(@RequestParam Long userId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select t.id,t.title,t.status,t.difficulty_level,t.production_time_minutes,t.created_at,k.id as kit_id,k.kit_name,k.price,k.stock,k.status as kit_status " +
                        "from tp_tutorial t left join tp_material_kit k on t.id=k.tutorial_id where t.author_user_id=? and t.status<>5 order by t.id desc", userId);
        return Result.success(list);
    }

    @PutMapping("/tutorial/{id}/kit")
    public Result updateTutorialKit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (!isTutorialOwner(id, userId)) {
            return Result.error("403", "无权限修改材料包");
        }
        saveOrUpdateKit(id, userId, body, true);
        return Result.success();
    }

    @PostMapping("/tutorial/{id}/like")
    public Result likeTutorial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (userId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId 不能为空");
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_like where tutorial_id=? and user_id=?", Integer.class, id, userId);
        if (count != null && count > 0) {
            return Result.success("已点赞");
        }
        jdbcTemplate.update("insert into tp_tutorial_like(tutorial_id,user_id) values(?,?)", id, userId);
        jdbcTemplate.update("update tp_tutorial set like_count=like_count+1,hot_score=hot_score+1 where id=?", id);
        return Result.success();
    }

    @PostMapping("/tutorial/{id}/favorite")
    public Result favoriteTutorial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (userId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId 不能为空");
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_favorite where tutorial_id=? and user_id=?", Integer.class, id, userId);
        if (count != null && count > 0) {
            return Result.success("已收藏");
        }
        jdbcTemplate.update("insert into tp_tutorial_favorite(tutorial_id,user_id) values(?,?)", id, userId);
        jdbcTemplate.update("update tp_tutorial set favorite_count=favorite_count+1,hot_score=hot_score+1.5 where id=?", id);
        return Result.success();
    }

    @PostMapping("/tutorial/{id}/unlike")
    public Result unlikeTutorial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (userId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId 不能为空");
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_like where tutorial_id=? and user_id=?", Integer.class, id, userId);
        if (count == null || count == 0) {
            return Result.success("未点赞");
        }
        jdbcTemplate.update("delete from tp_tutorial_like where tutorial_id=? and user_id=?", id, userId);
        jdbcTemplate.update("update tp_tutorial set like_count=if(like_count>0,like_count-1,0),hot_score=GREATEST(hot_score-1,0) where id=?", id);
        return Result.success();
    }

    @PostMapping("/tutorial/{id}/unfavorite")
    public Result unfavoriteTutorial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        if (userId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId 不能为空");
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_favorite where tutorial_id=? and user_id=?", Integer.class, id, userId);
        if (count == null || count == 0) {
            return Result.success("未收藏");
        }
        jdbcTemplate.update("delete from tp_tutorial_favorite where tutorial_id=? and user_id=?", id, userId);
        jdbcTemplate.update("update tp_tutorial set favorite_count=if(favorite_count>0,favorite_count-1,0),hot_score=GREATEST(hot_score-1.5,0) where id=?", id);
        return Result.success();
    }

    @PostMapping("/tutorial/{id}/comment")
    public Result commentTutorial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        String content = asString(body.get("content"));
        Long parentCommentId = toLong(body.get("parentCommentId"));
        if (userId == null || StrUtil.isBlank(content)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/content 不能为空");
        }
        jdbcTemplate.update("insert into tp_tutorial_comment(tutorial_id,user_id,parent_comment_id,content,status) values(?,?,?,?,1)",
                id, userId, parentCommentId, content);
                jdbcTemplate.update("update tp_tutorial set comment_count=comment_count+1,hot_score=hot_score+1 where id=?", id);
        return Result.success();
    }

    @DeleteMapping("/tutorial/comment/{commentId}")
    public Result deleteTutorialComment(@PathVariable Long commentId, @RequestParam Long userId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,tutorial_id,user_id,status from tp_tutorial_comment where id=?", commentId);
        if (list.isEmpty()) {
            return Result.error("404", "评论不存在");
        }
        Map<String, Object> comment = list.get(0);
        if (toInt(comment.get("status"), 2) != 1) {
            return Result.error("400", "评论不可删除");
        }
        Long ownerId = toLong(comment.get("user_id"));
        if (!userId.equals(ownerId)) {
            return Result.error("403", "无权限删除该评论");
        }
                Long tutorialId = toLong(comment.get("tutorial_id"));
        jdbcTemplate.update("delete from tp_tutorial_comment where id=?", commentId);
        jdbcTemplate.update("update tp_tutorial set comment_count=if(comment_count>0,comment_count-1,0) where id=?", tutorialId);
        return Result.success();
    }

    @PostMapping("/community/post")
    public Result addPost(@RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        String title = asString(body.get("title"));
        String content = asString(body.get("content"));
        if (userId == null || StrUtil.hasBlank(title, content)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/title/content 不能为空");
        }
        jdbcTemplate.update("insert into tp_community_post(author_user_id,title,content,status,hot_score) values(?,?,?,1,5)",
                userId, title, content);
        return Result.success();
    }

    @PutMapping("/community/post/{id}")
    public Result updatePost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        String title = asString(body.get("title"));
        String content = asString(body.get("content"));
        if (userId == null || StrUtil.hasBlank(title, content)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/title/content 不能为空");
        }
        Integer own = jdbcTemplate.queryForObject(
                "select count(1) from tp_community_post where id=? and author_user_id=? and status=1", Integer.class, id, userId);
        if (own == null || own == 0) {
            return Result.error("403", "无权限编辑该帖子");
        }
        jdbcTemplate.update("update tp_community_post set title=?,content=? where id=?", title, content, id);
        return Result.success();
    }

    @DeleteMapping("/community/post/{id}")
    public Result deletePost(@PathVariable Long id, @RequestParam Long userId) {
        Integer own = jdbcTemplate.queryForObject(
                "select count(1) from tp_community_post where id=? and author_user_id=? and status=1", Integer.class, id, userId);
        if (own == null || own == 0) {
            return Result.error("403", "无权限删除该帖子");
        }
        jdbcTemplate.update("update tp_community_post set status=3 where id=?", id);
        jdbcTemplate.update("update tp_community_post_comment set status=2 where post_id=?", id);
        return Result.success();
    }

    @PostMapping("/community/{id}/reply")
    public Result replyPost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        String content = asString(body.get("content"));
        Long parentCommentId = toLong(body.get("parentCommentId"));
        if (userId == null || StrUtil.isBlank(content)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/content 不能为空");
        }
        jdbcTemplate.update("insert into tp_community_post_comment(post_id,user_id,parent_comment_id,content,status) values(?,?,?,?,1)",
                id, userId, parentCommentId, content);
        jdbcTemplate.update("update tp_community_post set comment_count=comment_count+1,hot_score=hot_score+1 where id=?", id);
        return Result.success();
    }

    @DeleteMapping("/community/comment/{commentId}")
    public Result deletePostComment(@PathVariable Long commentId, @RequestParam Long userId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,post_id,user_id,status from tp_community_post_comment where id=?", commentId);
        if (list.isEmpty()) {
            return Result.error("404", "评论不存在");
        }
        Map<String, Object> comment = list.get(0);
        if (toInt(comment.get("status"), 2) != 1) {
            return Result.error("400", "评论不可删除");
        }
        Long ownerId = toLong(comment.get("user_id"));
        if (!userId.equals(ownerId)) {
            return Result.error("403", "无权限删除该评论");
        }
        Long postId = toLong(comment.get("post_id"));
        jdbcTemplate.update("update tp_community_post_comment set status=2 where id=?", commentId);
        jdbcTemplate.update("update tp_community_post set comment_count=if(comment_count>0,comment_count-1,0) where id=?", postId);
        return Result.success();
    }

    @PostMapping("/order/create")
    @Transactional(rollbackFor = Exception.class)
    public Result createOrder(@RequestBody Map<String, Object> body) {
        Long buyerUserId = toLong(body.get("buyerUserId"));
        Long materialKitId = toLong(body.get("materialKitId"));
        int quantity = Math.max(1, toInt(body.get("quantity"), 1));
        if (buyerUserId == null || materialKitId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "buyerUserId/materialKitId 不能为空");
        }
        List<Map<String, Object>> kits = jdbcTemplate.queryForList(
                "select k.id,k.tutorial_id,k.seller_user_id,k.kit_name,k.kit_desc,k.price,k.stock,k.status,t.cover_image_url as tutorial_cover " +
                        "from tp_material_kit k join tp_tutorial t on k.tutorial_id=t.id where k.id=? and t.status=2", materialKitId);
        if (kits.isEmpty()) {
            return Result.error("404", "材料包不存在");
        }
        Map<String, Object> kit = kits.get(0);
        if (toInt(kit.get("status"), 2) != 1) {
            return Result.error("4001", "材料包不可购买");
        }
        int stock = toInt(kit.get("stock"), 0);
        if (stock < quantity) {
            return Result.error(ResultCode.ORDER_PAY_ERROR.code, ResultCode.ORDER_PAY_ERROR.msg);
        }

        Long sellerUserId = toLong(kit.get("seller_user_id"));
        if (buyerUserId.equals(sellerUserId)) {
            return Result.error("4002", "不能购买自己的材料包");
        }

        BigDecimal unitPrice = toDecimal(kit.get("price"), BigDecimal.ZERO);
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));

        BigDecimal buyerBal = jdbcTemplate.queryForObject("select wallet_balance from tp_user where id=?", BigDecimal.class, buyerUserId);
        if (buyerBal == null) {
            buyerBal = BigDecimal.ZERO;
        }
        if (buyerBal.compareTo(totalAmount) < 0) {
            return Result.error(ResultCode.WALLET_INSUFFICIENT.code, ResultCode.WALLET_INSUFFICIENT.msg);
        }
        int dec = jdbcTemplate.update("update tp_user set wallet_balance=wallet_balance-? where id=? and wallet_balance>=?",
                totalAmount, buyerUserId, totalAmount);
        if (dec == 0) {
            return Result.error(ResultCode.WALLET_INSUFFICIENT.code, ResultCode.WALLET_INSUFFICIENT.msg);
        }
        jdbcTemplate.update("update tp_user set total_earnings=total_earnings+? where id=?", totalAmount, sellerUserId);

        String orderNo = "TP" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100);
        String kitDescSnap = emptyToNull(asString(kit.get("kit_desc")));
        String coverSnap = emptyToNull(asString(kit.get("tutorial_cover")));

        jdbcTemplate.update("insert into tp_material_kit_order(order_no,buyer_user_id,seller_user_id,material_kit_id,tutorial_id," +
                        "kit_name_snapshot,kit_desc_snapshot,cover_image_snapshot,unit_price,quantity,total_amount,order_status," +
                        "receiver_name,receiver_phone,receiver_address,remark,updated_at) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?,1,?,?,?,?,now())",
                orderNo, buyerUserId, sellerUserId, materialKitId, toLong(kit.get("tutorial_id")),
                asString(kit.get("kit_name")), kitDescSnap, coverSnap,
                unitPrice, quantity, totalAmount,
                emptyToNull(asString(body.get("receiverName"))), emptyToNull(asString(body.get("receiverPhone"))),
                emptyToNull(asString(body.get("receiverAddress"))), emptyToNull(asString(body.get("remark"))));

        jdbcTemplate.update(
                "update tp_material_kit set stock=stock-?,sales_count=sales_count+?,status=IF(stock-?<=0,3,status) where id=? and stock>=?",
                quantity, quantity, quantity, materialKitId, quantity);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orderNo", orderNo);
        return Result.success(result);
    }

    @GetMapping("/order/{orderId}")
    public Result orderDetail(@PathVariable Long orderId, @RequestParam Long userId) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select o.*, t.title as tutorial_title, t.cover_image_url as tutorial_cover_live " +
                        "from tp_material_kit_order o left join tp_tutorial t on o.tutorial_id=t.id where o.id=?",
                orderId);
        if (list.isEmpty()) {
            return Result.error("404", "订单不存在");
        }
        Map<String, Object> row = list.get(0);
        Long buyerId = toLong(row.get("buyer_user_id"));
        Long sellerId = toLong(row.get("seller_user_id"));
        if (!userId.equals(buyerId) && !userId.equals(sellerId)) {
            return Result.error("403", "无权查看该订单");
        }
        return Result.success(row);
    }

    @GetMapping("/order/my")
    public Result myOrders(@RequestParam Long userId) {
        List<Map<String, Object>> buyOrders = jdbcTemplate.queryForList(
                "select o.id,o.order_no,o.kit_name_snapshot,o.kit_desc_snapshot,o.cover_image_snapshot,o.unit_price,o.quantity,o.total_amount," +
                        "o.order_status,o.created_at,u.nickname as seller_name " +
                        "from tp_material_kit_order o left join tp_user u on o.seller_user_id=u.id where o.buyer_user_id=? order by o.id desc", userId);
        List<Map<String, Object>> sellOrders = jdbcTemplate.queryForList(
                "select o.id,o.order_no,o.kit_name_snapshot,o.kit_desc_snapshot,o.cover_image_snapshot,o.unit_price,o.quantity,o.total_amount," +
                        "o.order_status,o.created_at,u.nickname as buyer_name " +
                        "from tp_material_kit_order o left join tp_user u on o.buyer_user_id=u.id where o.seller_user_id=? order by o.id desc", userId);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("buyOrders", buyOrders);
        result.put("sellOrders", sellOrders);
        return Result.success(result);
    }

    @PostMapping("/order/{orderId}/status")
    public Result updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        Integer status = body.get("status") == null ? null : toInt(body.get("status"), 0);
        if (userId == null || status == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/status 不能为空");
        }
        List<Map<String, Object>> orders = jdbcTemplate.queryForList(
                "select id,buyer_user_id,seller_user_id,order_status from tp_material_kit_order where id=?", orderId);
        if (orders.isEmpty()) {
            return Result.error("404", "订单不存在");
        }
        Map<String, Object> order = orders.get(0);
        Long buyerId = toLong(order.get("buyer_user_id"));
        Long sellerId = toLong(order.get("seller_user_id"));
        int current = toInt(order.get("order_status"), 0);

        // 卖家：待发货(1) -> 运输中(2)，约 10 秒后自动变为待取货(3)
        if (userId.equals(sellerId) && current == 1 && status == 2) {
            int n = jdbcTemplate.update("update tp_material_kit_order set order_status=2,updated_at=now() where id=? and order_status=1", orderId);
            if (n == 0) {
                return Result.error("400", "订单状态已变更");
            }
            scheduleTransportToAwaitingPickup(orderId);
            return Result.success();
        }
                // 买家：待取货(3) -> 已取货(4)，卖家收益转入余额
        if (userId.equals(buyerId) && current == 3 && status == 4) {
            List<Map<String, Object>> detail = jdbcTemplate.queryForList(
                    "select total_amount from tp_material_kit_order where id=? and order_status=3", orderId);
            if (detail.isEmpty()) {
                return Result.error("400", "订单状态已变更");
            }
            BigDecimal amount = (BigDecimal) detail.get(0).get("total_amount");
            jdbcTemplate.update("update tp_material_kit_order set order_status=4,updated_at=now() where id=? and order_status=3", orderId);
            jdbcTemplate.update("update tp_user set wallet_balance=wallet_balance+?,total_earnings=total_earnings-? where id=?", amount, amount, sellerId);
            return Result.success();
        }
        return Result.error("403", "无权限或状态非法");
    }

    private void scheduleTransportToAwaitingPickup(Long orderId) {
        final Long oid = orderId;
        final JdbcTemplate jt = jdbcTemplate;
        TP_ORDER_SCHEDULER.schedule(() -> {
            try {
                jt.update("update tp_material_kit_order set order_status=3,updated_at=now() where id=? and order_status=2", oid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.SECONDS);
    }

    @GetMapping("/user/center")
    public Result userCenter(@RequestParam Long userId) {
        Integer myFavorites = jdbcTemplate.queryForObject("select count(1) from tp_tutorial_favorite where user_id=?", Integer.class, userId);
        Integer myTutorials = jdbcTemplate.queryForObject("select count(1) from tp_tutorial where author_user_id=? and status<>5", Integer.class, userId);
        Integer myPosts = jdbcTemplate.queryForObject("select count(1) from tp_community_post where author_user_id=? and status<>3", Integer.class, userId);
        Integer myBuyOrders = jdbcTemplate.queryForObject("select count(1) from tp_material_kit_order where buyer_user_id=?", Integer.class, userId);
        Integer mySellOrders = jdbcTemplate.queryForObject("select count(1) from tp_material_kit_order where seller_user_id=?", Integer.class, userId);

        List<Map<String, Object>> favorites = jdbcTemplate.queryForList(
                "select t.id,t.title,t.cover_image_url,t.summary,f.created_at as favorite_time,u.nickname as author_name " +
                        "from tp_tutorial_favorite f left join tp_tutorial t on f.tutorial_id=t.id left join tp_user u on t.author_user_id=u.id where f.user_id=? order by f.id desc",
                userId);
        List<Map<String, Object>> tutorials = jdbcTemplate.queryForList(
                "select id,title,status,created_at from tp_tutorial where author_user_id=? and status<>5 order by id desc", userId);
        List<Map<String, Object>> posts = jdbcTemplate.queryForList(
                "select id,title,status,created_at from tp_community_post where author_user_id=? and status<>3 order by id desc", userId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("myFavorites", myFavorites == null ? 0 : myFavorites);
        data.put("myTutorials", myTutorials == null ? 0 : myTutorials);
        data.put("myPosts", myPosts == null ? 0 : myPosts);
        data.put("myBuyOrders", myBuyOrders == null ? 0 : myBuyOrders);
        data.put("mySellOrders", mySellOrders == null ? 0 : mySellOrders);
        data.put("favoriteList", favorites);
        data.put("tutorialList", tutorials);
        data.put("postList", posts);
        List<Map<String, Object>> wlist = jdbcTemplate.queryForList("select wallet_balance,total_earnings from tp_user where id=?", userId);
        if (!wlist.isEmpty()) {
            Map<String, Object> walletRow = wlist.get(0);
            data.put("walletBalance", walletRow.get("wallet_balance"));
            data.put("totalEarnings", walletRow.get("total_earnings"));
        }
        return Result.success(data);
    }

    // ========================
    // 管理员功能
    // ========================
    @GetMapping("/admin/users")
    public Result adminUsers(@RequestParam Long adminUserId,
                             @RequestParam(required = false) String keyword) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        String kw = keyword == null ? "" : keyword.trim();
        List<Map<String, Object>> list;
        if (kw.isEmpty()) {
            list = jdbcTemplate.queryForList(
                    "select id,username,nickname,phone,role_type,status,created_at from tp_user order by id desc");
        } else {
            String safe = kw.replace("%", "").replace("_", "");
            String like = "%" + safe + "%";
            list = jdbcTemplate.queryForList(
                    "select id,username,nickname,phone,role_type,status,created_at from tp_user where " +
                            "cast(id as char) like ? or username like ? or nickname like ? or ifnull(phone,'') like ?",
                    like, like, like, like);
            list.sort((a, b) -> Integer.compare(userSearchMatchScore(b, kw), userSearchMatchScore(a, kw)));
        }
        return Result.success(list);
    }

    @PostMapping("/admin/user/toggle")
    public Result adminToggleUser(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long userId = toLong(body.get("userId"));
        Integer status = body.get("status") == null ? null : toInt(body.get("status"), 1);
        if (userId == null || status == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/status 不能为空");
        }
        jdbcTemplate.update("update tp_user set status=? where id=? and role_type<>9", status, userId);
        return Result.success();
    }

    @PostMapping("/admin/user/reset-password")
    public Result adminResetPassword(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long userId = toLong(body.get("userId"));
        String newPassword = asString(body.get("newPassword"));
        if (userId == null || StrUtil.isBlank(newPassword)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "userId/newPassword 不能为空");
        }
        jdbcTemplate.update("update tp_user set password_hash=? where id=?", SecureUtil.md5(newPassword), userId);
        return Result.success();
    }

    @PostMapping("/admin/tutorial/remove")
    public Result adminRemoveTutorial(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long tutorialId = toLong(body.get("tutorialId"));
        if (tutorialId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "tutorialId 不能为空");
        }
                                jdbcTemplate.update("update tp_tutorial set status=5 where id=?", tutorialId);
        // 级联删除该教程的收藏、点赞、评论
        jdbcTemplate.update("delete from tp_tutorial_favorite where tutorial_id=?", tutorialId);
        jdbcTemplate.update("delete from tp_tutorial_like where tutorial_id=?", tutorialId);
        jdbcTemplate.update("delete from tp_tutorial_comment where tutorial_id=?", tutorialId);
        return Result.success();
    }

    /** 管理端查看教程详情（含步骤、材料关联） */
    @GetMapping("/admin/tutorial/{tutorialId}/detail")
    public Result adminTutorialFullDetail(@PathVariable Long tutorialId, @RequestParam Long adminUserId) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        List<Map<String, Object>> tutorials = jdbcTemplate.queryForList(
                "select t.*,u.nickname as author_name from tp_tutorial t left join tp_user u on t.author_user_id=u.id where t.id=? and t.status<>5",
                tutorialId);
        if (tutorials.isEmpty()) {
            return Result.error("404", "教程不存在或已删除");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("tutorial", tutorials.get(0));
        data.put("steps", jdbcTemplate.queryForList(
                "select id,step_no,step_title,step_content,step_image_url,created_at from tp_tutorial_step where tutorial_id=? order by step_no asc",
                tutorialId));
        data.put("materials", jdbcTemplate.queryForList(
                "select m.id,m.material_name from tp_tutorial_material_rel r left join tp_material_dict m on r.material_id=m.id where r.tutorial_id=?",
                tutorialId));
                List<Map<String, Object>> kits = jdbcTemplate.queryForList(
                "select id,kit_name,kit_desc,price,stock,sales_count,status from tp_material_kit where tutorial_id=? and status<>4", tutorialId);
        data.put("materialKit", kits.isEmpty() ? null : kits.get(0));
        return Result.success(data);
    }

    @GetMapping("/admin/dashboard-stats")
    public Result adminDashboardStats(@RequestParam Long adminUserId) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        Map<String, Object> stats = new HashMap<>();
        // 用户总数 / 禁用账号
        List<Map<String, Object>> users = jdbcTemplate.queryForList("select id,status from tp_user");
        stats.put("userCount", users.size());
        stats.put("disabledUserCount", users.stream().filter(u -> toInt(u.get("status"), 1) == 0).count());
        // 教程数 / 已删除教程
        List<Map<String, Object>> tutorials = jdbcTemplate.queryForList("select id,status from tp_tutorial");
        stats.put("tutorialCount", tutorials.stream().filter(t -> toInt(t.get("status"), 0) != 5).count());
        stats.put("deletedTutorialCount", tutorials.stream().filter(t -> toInt(t.get("status"), 0) == 5).count());
        // 帖子数 / 已删除帖子
        List<Map<String, Object>> posts = jdbcTemplate.queryForList("select id,status from tp_community_post");
        stats.put("postCount", posts.stream().filter(p -> toInt(p.get("status"), 0) != 3).count());
        stats.put("deletedPostCount", posts.stream().filter(p -> toInt(p.get("status"), 0) == 3).count());
                                // 评论总数（与评论管理列表一致：排除已删帖子的评论和已删除评论）+ 已删除评论（status=3）
                List<Map<String, Object>> visiblePostComments = jdbcTemplate.queryForList(
                        "select c.id from tp_community_post_comment c " +
                        "left join tp_community_post p on c.post_id=p.id where p.status<>3 and c.status<>3");
                List<Map<String, Object>> tutorialComments = jdbcTemplate.queryForList("select id,status from tp_tutorial_comment");
                long visibleTutorialComments = tutorialComments.stream().filter(c -> toInt(c.get("status"), 2) != 3).count();
                long deletedTutorialComments = tutorialComments.stream().filter(c -> toInt(c.get("status"), 2) == 3).count();
                List<Map<String, Object>> allPostComments = jdbcTemplate.queryForList("select id,status from tp_community_post_comment");
                long deletedPostComments = allPostComments.stream().filter(c -> toInt(c.get("status"), 2) == 3).count();
                stats.put("commentCount", visiblePostComments.size() + visibleTutorialComments);
                stats.put("deletedCommentCount", deletedPostComments + deletedTutorialComments);
                // 材料包总数（与商品管理一致：仅公开教程且未删除）/ 已删除材料包
        List<Map<String, Object>> activeKits = jdbcTemplate.queryForList(
                "select k.id from tp_material_kit k left join tp_tutorial t on k.tutorial_id=t.id where k.status<>4 and t.status=2");
        List<Map<String, Object>> allKits = jdbcTemplate.queryForList("select id,status from tp_material_kit");
        stats.put("kitCount", activeKits.size());
        stats.put("deletedKitCount", allKits.stream().filter(k -> toInt(k.get("status"), 1) == 4).count());
        // 近期高热帖子
        List<Map<String, Object>> hotPosts = jdbcTemplate.queryForList(
                "select p.id,p.title,p.hot_score,p.comment_count,u.nickname as author_name " +
                "from tp_community_post p left join tp_user u on p.author_user_id=u.id " +
                "where p.status<>3 order by p.hot_score desc limit 8");
        stats.put("hotPosts", hotPosts);
        return Result.success(stats);
    }

    /** 禁止公开：前台仅展示 status=2，3 为管理员封禁 */
    @PostMapping("/admin/tutorial/ban-public")
    public Result adminTutorialBanPublic(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long tutorialId = toLong(body.get("tutorialId"));
        Integer targetStatus = body.get("targetStatus") == null ? null : toInt(body.get("targetStatus"), -1);
        if (tutorialId == null || targetStatus == null || (targetStatus != 2 && targetStatus != 3)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "tutorialId/targetStatus 非法");
        }
        jdbcTemplate.update("update tp_tutorial set status=? where id=? and status<>5", targetStatus, tutorialId);
        return Result.success();
    }

                @GetMapping("/admin/tutorials")
    public Result adminTutorials(@RequestParam Long adminUserId,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer status) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        StringBuilder sql = new StringBuilder(
                "select t.id,t.title,t.status,t.difficulty_level,t.production_time_minutes,t.hot_score,t.created_at,u.nickname as author_name " +
                "from tp_tutorial t left join tp_user u on t.author_user_id=u.id where t.status<>5");
        List<Object> params = new ArrayList<>();
        if (StrUtil.isNotBlank(keyword)) {
            String kw = keyword.trim();
            sql.append(" and (t.id=? or t.title like ? or u.nickname like ?)");
            params.add(kw);
            params.add("%" + kw + "%");
            params.add("%" + kw + "%");
        }
        if (status != null) {
            sql.append(" and t.status=?");
            params.add(status);
        }
        sql.append(" order by t.id desc");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    @PostMapping("/admin/community/remove")
    public Result adminRemoveCommunity(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long postId = toLong(body.get("postId"));
        if (postId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "postId 不能为空");
        }
                jdbcTemplate.update("update tp_community_post set status=3 where id=?", postId);
        jdbcTemplate.update("update tp_community_post_comment set status=2 where post_id=? and status=1", postId);
        return Result.success();
    }

    /** 帖子：status=1正常/2违规/3物理删除(含评论) */
    @PostMapping("/admin/post/set-status")
    public Result adminPostSetStatus(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long postId = toLong(body.get("postId"));
        Integer status = body.get("status") == null ? null : toInt(body.get("status"), -1);
        if (postId == null || status == null || (status != 1 && status != 2 && status != 3)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "postId/status 非法");
        }
                if (status == 3) {
                    jdbcTemplate.update("update tp_community_post_comment set status=2 where post_id=? and status=1", postId);
            jdbcTemplate.update("update tp_community_post set status=3 where id=?", postId);
            return Result.success();
        }
        jdbcTemplate.update("update tp_community_post set status=? where id=?", status, postId);
        return Result.success();
    }

                @GetMapping("/admin/posts")
    public Result adminPosts(@RequestParam Long adminUserId,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Integer status) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        StringBuilder sql = new StringBuilder(
                "select p.id,p.title,p.status,p.view_count,p.comment_count,p.hot_score,p.created_at,u.nickname as author_name " +
                "from tp_community_post p left join tp_user u on p.author_user_id=u.id where p.status<>3");
        List<Object> params = new ArrayList<>();
        if (StrUtil.isNotBlank(keyword)) {
            String kw = keyword.trim();
            sql.append(" and (p.id=? or p.title like ? or u.nickname like ?)");
            params.add(kw);
            params.add("%" + kw + "%");
            params.add("%" + kw + "%");
        }
                if (status != null) {
            sql.append(" and p.status=?");
            params.add(status);
        }
        sql.append(" order by p.id desc");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

                @GetMapping("/admin/post-comments")
    public Result adminPostComments(@RequestParam Long adminUserId,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Integer status) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        StringBuilder sql = new StringBuilder(
                "select c.id,c.post_id,c.content,c.status,c.created_at,u.nickname as user_name," +
                "p.title as post_title from tp_community_post_comment c " +
                "left join tp_user u on c.user_id=u.id " +
                "left join tp_community_post p on c.post_id=p.id where p.status<>3 and c.status<>3");
        List<Object> params = new ArrayList<>();
        if (StrUtil.isNotBlank(keyword)) {
            String kw = keyword.trim();
            sql.append(" and (c.content like ? or p.title like ? or u.nickname like ?)");
            params.add("%" + kw + "%");
            params.add("%" + kw + "%");
            params.add("%" + kw + "%");
        }
        if (status != null) {
            sql.append(" and c.status=?");
            params.add(status);
        }
        sql.append(" order by c.id desc");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

        /** 帖子评论：1正常 2违规 */
    /** 教程评论：1正常 2违规 */
        @GetMapping("/admin/tutorial-comments")
    public Result adminTutorialComments(@RequestParam Long adminUserId,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer status) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        StringBuilder sql = new StringBuilder(
                "select c.id,c.tutorial_id,t.title as tutorial_title,c.content,c.status,c.created_at,u.nickname as user_name " +
                "from tp_tutorial_comment c " +
                "left join tp_user u on c.user_id=u.id " +
                "left join tp_tutorial t on c.tutorial_id=t.id where c.status<>3");
        List<Object> params = new ArrayList<>();
        if (StrUtil.isNotBlank(keyword)) {
            String kw = keyword.trim();
            sql.append(" and (c.content like ? or t.title like ? or u.nickname like ?)");
            params.add("%" + kw + "%");
            params.add("%" + kw + "%");
            params.add("%" + kw + "%");
        }
        if (status != null) {
            sql.append(" and c.status=?");
            params.add(status);
        }
        sql.append(" order by c.id desc");
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    /** 教程评论：1正常 2违规 */
    @PostMapping("/admin/tutorial-comment/set-status")
    public Result adminTutorialCommentSetStatus(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long commentId = toLong(body.get("commentId"));
        Integer status = body.get("status") == null ? null : toInt(body.get("status"), -1);
        if (commentId == null || status == null || (status != 1 && status != 2)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "commentId/status 非法");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,tutorial_id,status from tp_tutorial_comment where id=?", commentId);
        if (list.isEmpty()) {
            return Result.error("404", "评论不存在");
        }
        int old = toInt(list.get(0).get("status"), 2);
        Long tutorialId = toLong(list.get(0).get("tutorial_id"));
        jdbcTemplate.update("update tp_tutorial_comment set status=? where id=?", status, commentId);
        if (old == 1 && status == 2) {
            jdbcTemplate.update("update tp_tutorial set comment_count=if(comment_count>0,comment_count-1,0) where id=?", tutorialId);
        } else if (old == 2 && status == 1) {
            jdbcTemplate.update("update tp_tutorial set comment_count=comment_count+1 where id=?", tutorialId);
        }
        return Result.success();
    }

    /** 管理员删除教程评论 */
    @PostMapping("/admin/tutorial-comment/remove")
    public Result adminRemoveTutorialComment(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long commentId = toLong(body.get("commentId"));
        if (commentId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "commentId 不能为空");
        }
                List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,tutorial_id,status from tp_tutorial_comment where id=? and status<>3", commentId);
        if (list.isEmpty()) {
            return Result.error("404", "评论不存在或已删除");
        }
        Long tutorialId = toLong(list.get(0).get("tutorial_id"));
        int old = toInt(list.get(0).get("status"), 2);
        jdbcTemplate.update("update tp_tutorial_comment set status=3 where id=?", commentId);
        if (old == 1) {
            jdbcTemplate.update("update tp_tutorial set comment_count=if(comment_count>0,comment_count-1,0) where id=?", tutorialId);
        }
        return Result.success();
    }

    /** 管理员删除帖子评论 */
    @PostMapping("/admin/post-comment/remove")
    public Result adminRemovePostComment(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long commentId = toLong(body.get("commentId"));
        if (commentId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "commentId 不能为空");
        }
                List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,post_id,status from tp_community_post_comment where id=? and status<>3", commentId);
        if (list.isEmpty()) {
            return Result.error("404", "评论不存在或已删除");
        }
        Long postId = toLong(list.get(0).get("post_id"));
        int old = toInt(list.get(0).get("status"), 2);
        jdbcTemplate.update("update tp_community_post_comment set status=3 where id=?", commentId);
        if (old == 1) {
            jdbcTemplate.update("update tp_community_post set comment_count=if(comment_count>0,comment_count-1,0) where id=?", postId);
        }
        return Result.success();
    }

    @PostMapping("/admin/post-comment/set-status")
    public Result adminPostCommentSetStatus(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long commentId = toLong(body.get("commentId"));
        Integer status = body.get("status") == null ? null : toInt(body.get("status"), -1);
        if (commentId == null || status == null || (status != 1 && status != 2)) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "commentId/status 非法");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select id,post_id,status from tp_community_post_comment where id=?", commentId);
        if (list.isEmpty()) {
            return Result.error("404", "评论不存在");
        }
        int old = toInt(list.get(0).get("status"), 2);
        Long postId = toLong(list.get(0).get("post_id"));
        jdbcTemplate.update("update tp_community_post_comment set status=? where id=?", status, commentId);
        if (old == 1 && status == 2) {
            jdbcTemplate.update("update tp_community_post set comment_count=if(comment_count>0,comment_count-1,0) where id=?", postId);
        } else if (old == 2 && status == 1) {
            jdbcTemplate.update("update tp_community_post set comment_count=comment_count+1 where id=?", postId);
        }
        return Result.success();
    }

    @PostMapping("/admin/kit/off-shelf")
    public Result adminOffShelfKit(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long kitId = toLong(body.get("kitId"));
        if (kitId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "kitId 不能为空");
        }
        jdbcTemplate.update("update tp_material_kit set status=2 where id=?", kitId);
        return Result.success();
    }

    @PostMapping("/admin/kit/delete")
    public Result adminDeleteKit(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long kitId = toLong(body.get("kitId"));
        if (kitId == null) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "kitId 不能为空");
        }
        jdbcTemplate.update("update tp_material_kit set status=4 where id=?", kitId);
        return Result.success();
    }

    /** 材料包状态：1在售 2停售 3售完 4已删除 */
    @PostMapping("/admin/kit/set-status")
    public Result adminKitSetStatus(@RequestBody Map<String, Object> body) {
        Long adminUserId = toLong(body.get("adminUserId"));
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可操作");
        }
        Long kitId = toLong(body.get("kitId"));
        Integer status = body.get("status") == null ? null : toInt(body.get("status"), -1);
        if (kitId == null || status == null || status < 1 || status > 4) {
            return Result.error(ResultCode.PARAM_LOST_ERROR.code, "kitId/status 非法");
        }
        jdbcTemplate.update("update tp_material_kit set status=? where id=? and status<>4", status, kitId);
        return Result.success();
    }

    @GetMapping("/admin/kits")
    public Result adminKits(@RequestParam Long adminUserId) {
        if (!isAdmin(adminUserId)) {
            return Result.error("403", "仅管理员可访问");
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "select k.id,k.tutorial_id,k.kit_name,k.kit_desc,k.price,k.stock,k.sales_count,k.status,k.created_at,t.title as tutorial_title,u.nickname as seller_name " +
                        "from tp_material_kit k " +
                        "left join tp_tutorial t on k.tutorial_id=t.id " +
                        "left join tp_user u on k.seller_user_id=u.id " +
                        "where k.status<>4 and t.status=2 order by k.id desc");
        return Result.success(list);
    }

    // ========================
    // 内部工具方法
    // ========================

    /** 用户搜索匹配权重：完全匹配 > 前缀 > 包含（取各字段最高再比行） */
    private static int userSearchMatchScore(Map<String, Object> row, String kw) {
        if (kw == null || kw.isEmpty()) {
            return 0;
        }
        int best = 0;
        best = Math.max(best, fieldMatchLevel(String.valueOf(row.get("id")), kw));
        best = Math.max(best, fieldMatchLevel(asString(row.get("username")), kw));
        best = Math.max(best, fieldMatchLevel(asString(row.get("nickname")), kw));
        best = Math.max(best, fieldMatchLevel(asString(row.get("phone")), kw));
        return best;
    }

    private static int fieldMatchLevel(String field, String kw) {
        if (field == null) {
            field = "";
        }
        if (field.equals(kw)) {
            return 1000;
        }
        if (field.startsWith(kw)) {
            return 500;
        }
        if (field.contains(kw)) {
            return 100;
        }
        return 0;
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_user where id=? and role_type=9 and status=1", Integer.class, userId);
        return count != null && count > 0;
    }

    private boolean isTutorialOwner(Long tutorialId, Long userId) {
        if (tutorialId == null || userId == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_tutorial where id=? and author_user_id=?", Integer.class, tutorialId, userId);
        return count != null && count > 0;
    }

    private void saveStepsAndRelations(Long tutorialId, Map<String, Object> body) {
        Object stepsObj = body.get("steps");
        if (stepsObj instanceof List) {
            List list = (List) stepsObj;
            for (int i = 0; i < list.size(); i++) {
                Object stepObj = list.get(i);
                if (!(stepObj instanceof Map)) {
                    continue;
                }
                Map step = (Map) stepObj;
                String stepTitle = asString(step.get("stepTitle"));
                String stepContent = asString(step.get("stepContent"));
                String stepImageUrl = asString(step.get("stepImageUrl"));
                if (StrUtil.isBlank(stepContent)) {
                    continue;
                }
                jdbcTemplate.update("insert into tp_tutorial_step(tutorial_id,step_no,step_title,step_content,step_image_url) values(?,?,?,?,?)",
                        tutorialId, i + 1, emptyToNull(stepTitle), stepContent, emptyToNull(stepImageUrl));
            }
        }

        Object materialIdsObj = body.get("materialIds");
        if (materialIdsObj instanceof List) {
            List materials = (List) materialIdsObj;
            for (Object m : materials) {
                Integer mid = toInt(m, 0);
                if (mid > 0) {
                    jdbcTemplate.update("insert into tp_tutorial_material_rel(tutorial_id,material_id) values(?,?)", tutorialId, mid);
                }
            }
        }

        Object tagIdsObj = body.get("tagIds");
        if (tagIdsObj instanceof List) {
            List tags = (List) tagIdsObj;
            for (Object t : tags) {
                Integer tid = toInt(t, 0);
                if (tid > 0) {
                    jdbcTemplate.update("insert into tp_tutorial_tag_rel(tutorial_id,tag_id) values(?,?)", tutorialId, tid);
                }
            }
        }
    }

    private void saveOrUpdateKit(Long tutorialId, Long userId, Map<String, Object> body, boolean upsert) {
        Object bindMaterialKitObj = body.get("bindMaterialKit");
        boolean bindMaterialKit = bindMaterialKitObj != null && Boolean.parseBoolean(String.valueOf(bindMaterialKitObj));
        if (!bindMaterialKit) {
            return;
        }

        String kitName = asString(body.get("kitName"));
        BigDecimal price = toDecimal(body.get("kitPrice"), BigDecimal.ZERO);
        int stock = toInt(body.get("kitStock"), 0);
        String kitDesc = asString(body.get("kitDesc"));

        if (StrUtil.isBlank(kitName) || price.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Integer count = jdbcTemplate.queryForObject("select count(1) from tp_material_kit where tutorial_id=?", Integer.class, tutorialId);
        if (upsert && count != null && count > 0) {
            jdbcTemplate.update("update tp_material_kit set kit_name=?,kit_desc=?,price=?,stock=?,status=1 where tutorial_id=?",
                    kitName, emptyToNull(kitDesc), price, stock, tutorialId);
        } else {
            jdbcTemplate.update("insert into tp_material_kit(tutorial_id,seller_user_id,kit_name,kit_desc,price,stock,status) values(?,?,?,?,?,?,1)",
                    tutorialId, userId, kitName, emptyToNull(kitDesc), price, stock);
        }
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private String emptyToNull(String value) {
        return StrUtil.isBlank(value) ? null : value;
    }

    private Integer toInt(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal toDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Map<String, Object> mapOf(Object k1, Object v1, Object k2, Object v2) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(String.valueOf(k1), v1);
        m.put(String.valueOf(k2), v2);
        return m;
    }
}