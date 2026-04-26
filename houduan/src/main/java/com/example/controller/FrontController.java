package com.example.controller;
import com.example.exception.CustomException;
import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.*;
import com.example.service.*;
import com.example.service.impl.CollectServiceImpl;
import com.example.vo.YonghuxinxiVo;
import com.example.vo.ErshouwupinVo;
import com.example.vo.DingdanVo;

import com.example.vo.PinglunVo;
import com.example.vo.BianhuantuVo;
import com.example.vo.XinwentongzhiVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 前端控制器
 * 处理前端相关请求，包括用户信息、二手物品、订单、评论等功能的API接口
 */
@RestController
@RequestMapping(value = "/front")
public class FrontController {
    // 自动注入用户信息服务
    @Autowired
private YonghuxinxiService  yonghuxinxiService;
    // 自动注入二手物品服务
@Autowired
private ErshouwupinService  ershouwupinService;
    // 自动注入订单服务
@Autowired
private DingdanService  dingdanService;

    // 自动注入评论服务
    @Autowired
    private PinglunService pinglunService;
    // 自动注入变换图服务
	@Autowired
    private BianhuantuService  bianhuantuService;
    // 自动注入问题通知服务
    @Autowired
    private XinwentongzhiService  xinwentongzhiService;
    // 自动注入留言板服务
    @Autowired
    private LiuyanbanService liuyanbanService;
    // 自动注入收藏服务实现类
    @Resource
    private CollectServiceImpl collectService;

    /**
     * 获取用户详情信息
     * @param id 用户ID
     * @return 返回用户详情信息
     */
	@GetMapping("/getyonghuxinxidetail/{id}")
public Result getyonghuxinxidetail(@PathVariable Integer id) {
	return Result.success(yonghuxinxiService.findById(id));
}
    /**
     * 分页查询用户信息
     * @param yonghuxinxiVo 用户信息查询条件
     * @return 返回分页查询结果
     */
@PostMapping("/yonghuxinxi")
public Result<YonghuxinxiVo> yonghuxinxi(@RequestBody YonghuxinxiVo yonghuxinxiVo) {
return Result.success(yonghuxinxiService.findPage(yonghuxinxiVo));
}
    /**
     * 注册用户
     * @param yonghuxinxi 用户信息
     * @return 返回操作结果
     */
@PostMapping("/zhuceyonghuxinxi")
public Result zhuceyonghuxinxi(@RequestBody Yonghuxinxi yonghuxinxi) {
yonghuxinxiService.add(yonghuxinxi);
return Result.success();
}
    /**
     * 根据用户名查询用户信息
     * @param yonghuxinxi 用户信息
     * @return 返回查询结果
     */
@PostMapping("/findyonghuxinxiyonghuming")
public Result findyonghuxinxiyonghuming(@RequestBody Yonghuxinxi yonghuxinxi) {
yonghuxinxiService.findyonghuxinxiyonghuming(yonghuxinxi);
return Result.success();
}
    /**
     * 用户登录
     * @param yonghuxinxi 用户登录信息
     * @return 返回登录结果
     */
@PostMapping("/loginyonghuxinxi")
public Result loginyonghuxinxi(@RequestBody Yonghuxinxi yonghuxinxi) {
return Result.success(yonghuxinxiService.login(yonghuxinxi));
}
    /**
     * 获取二手物品详情
     * @param id 物品ID
     * @return 返回物品详情信息
     */
@GetMapping("/getershouwupindetail/{id}")
public Result getershouwupindetail(@PathVariable Integer id) {
	return Result.success(ershouwupinService.findById(id));
}
    /**
     * 分页查询二手物品
     * @param ershouwupinVo 物品查询条件
     * @return 返回分页查询结果
     */
@PostMapping("/ershouwupin")
public Result<ErshouwupinVo> ershouwupin(@RequestBody ErshouwupinVo ershouwupinVo) {
return Result.success(ershouwupinService.findPage(ershouwupinVo));
}
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 返回订单详情信息
     */
@GetMapping("/getdingdandetail/{id}")
public Result getdingdandetail(@PathVariable Integer id) {
	return Result.success(dingdanService.findById(id));
}
    /**
     * 创建订单
     * @param dingdan 订单信息
     * @return 返回创建结果
     */
@PostMapping("/postdingdan")
public Result<Dingdan> postdingdan(@RequestBody Dingdan dingdan) {
return Result.success(dingdanService.add(dingdan));
}

	
    /**
     * 添加评论
     * @param pinglun 评论信息
     * @return 返回添加结果
     */
	@PostMapping("/postpinglun")
    public Result<Pinglun> postpinglun(@RequestBody Pinglun pinglun) {
        return Result.success(pinglunService.add(pinglun));
    }
    /**
     * 分页查询评论
     * @param pinglunVo 评论查询条件
     * @return 返回分页查询结果
     */
    @PostMapping("/pinglun")
    public Result<PinglunVo> pinglun(@RequestBody PinglunVo pinglunVo) {
        return Result.success(pinglunService.findPage(pinglunVo));
    }
	
    /**
     * 获取变换图列表
     * @return 返回变换图列表
     */
	@GetMapping("/bianhuantu")
    public Result bianhuantu() {
        return Result.success(bianhuantuService.list());
    }
   
    /**
     * 获取问题通知列表
     * @return 返回问题通知列表
     */
    @GetMapping("/xinwentongzhi")
    public Result xinwentongzhi() {
        return Result.success(xinwentongzhiService.list());
    }
    /**
     * 获取问题通知详情
     * @param id 通知ID
     * @return 返回通知详情信息
     */
    @GetMapping("/getxinwentongzhi/{id}")
    public Result getxinwentongzhi(@PathVariable Integer id) {
        return Result.success(xinwentongzhiService.findById(id));
    }
    /**
     * 添加留言
     * @param liuyanban 留言信息
     * @return 返回添加结果
     */
    @PostMapping("/addLiuyan")
    public Result addComment(@RequestBody Liuyanban liuyanban) {
        liuyanbanService.add(liuyanban);
        return Result.success();
    }
    /**
     * 获取留言列表
     * @return 返回留言列表
     */
    @GetMapping("/getLiuyan")
    public Result<List<Liuyanban>> getCommentTiezi() {
        return Result.success(liuyanbanService.getLiuyan());
    }
    /**
     * 添加收藏
     * @param collect 收藏信息
     * @return 返回添加结果
     */
    @PostMapping("/addCollect")
    public Result addCollect(@RequestBody Collect collect) {
        Collect collect1 = collectService.add(collect);
        if(collect1  == null){
            return Result.success("请勿重复收藏");
        }
        return Result.success();
    }
    /**
     * 删除收藏
     * @param id 收藏ID
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteCollect/{id}")
    public Result deleteCollect(@PathVariable Integer id) {
        collectService.delete(id);
        return Result.success();
    }
    /**
     * 获取用户收藏列表
     * @param collect 收藏查询条件
     * @return 返回收藏列表
     */
	@PostMapping("/getUserCollect")
    public Result getUserCollect(@RequestBody Collect collect) {
        return Result.success(collectService.findFrontPages(collect.getUserId()));
    }
    /**
     * 根据用户ID删除收藏
     * @param id 收藏ID
     * @param userId 用户ID
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteCollect/{id}/{userId}")
    public Result deleteCollect(@PathVariable Integer id,@PathVariable Integer userId) {
        collectService.deleteByUserId(id,userId);
        return Result.success();
    }

}
