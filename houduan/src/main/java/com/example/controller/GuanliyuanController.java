package com.example.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.Guanliyuan;
import com.example.exception.CustomException;
import com.example.service.GuanliyuanService;
import com.example.interceptor.utils.MapWrapperUtils;
import com.example.interceptor.utils.jwt.JwtUtil;
import com.example.vo.GuanliyuanVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员相关的RESTful API，包括增删改查、登录和密码修改等功能
 */
@RestController
@RequestMapping(value = "/guanliyuan")
public class GuanliyuanController {

    @Resource
    private GuanliyuanService guanliyuanService; // 注入管理员服务接口

    /**
     * 添加管理员
     * @param guanliyuan 管理员视图对象，包含要添加的管理员信息
     * @return 返回操作结果，包含添加的管理员信息
     */
    @PostMapping
    public Result<Guanliyuan> add(@RequestBody GuanliyuanVo guanliyuan) {
        guanliyuanService.add(guanliyuan);
           return Result.success(guanliyuan);
    }

    /**
     * 批量删除管理员
     * @param guanliyuan 管理员视图对象，包含要删除的管理员ID列表
     * @return 返回操作结果
     */
    @PostMapping("/deleteList")
    public Result<Guanliyuan> deleteList(@RequestBody GuanliyuanVo guanliyuan) {
        guanliyuanService.deleteList(guanliyuan.getList());
        return Result.success();
    }

    /**
     * 根据ID删除管理员
     * @param id 管理员ID
     * @return 返回操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        guanliyuanService.delete(id);
        return Result.success();
    }

    /**
     * 更新管理员信息
     * @param guanliyuan 管理员视图对象，包含要更新的管理员信息
     * @return 返回操作结果
     */
    @PutMapping
    public Result update(@RequestBody GuanliyuanVo guanliyuan) {
        guanliyuanService.update(guanliyuan);
        return Result.success();
    }

    /**
     * 根据ID查询管理员详情
     * @param id 管理员ID
     * @return 返回操作结果，包含查询到的管理员信息
     */
    @GetMapping("/{id}")
    public Result<Guanliyuan> detail(@PathVariable Integer id) {
        Guanliyuan guanliyuan = guanliyuanService.findById(id);
        return Result.success(guanliyuan);
    }

    /**
     * 查询所有管理员
     * @return 返回操作结果，包含所有管理员列表
     */
    @GetMapping
    public Result<List<Guanliyuan>> all() {
        return Result.success(guanliyuanService.list());
    }

    /**
     * 分页查询管理员
     * @param guanliyuanVo 管理员视图对象，包含分页查询条件
     * @return 返回操作结果，包含分页查询结果
     */
    @PostMapping("/page")
    public Result<GuanliyuanVo> page(@RequestBody GuanliyuanVo guanliyuanVo) {
        return Result.success(guanliyuanService.findPage(guanliyuanVo));
    }
    /**
     * 管理员登录
     * @param guanliyuan 管理员对象，包含用户名和密码
     * @param request HTTP请求对象
     * @return 返回操作结果，包含登录后的用户信息和token
     */
	    @PostMapping("/login")
    public Result login(@RequestBody Guanliyuan guanliyuan, HttpServletRequest request) {
        // 检查用户名和密码是否为空
        if (StrUtil.isBlank(guanliyuan.getYonghuming()) || StrUtil.isBlank(guanliyuan.getMima())) {
            throw new CustomException(ResultCode.PARAM_LOST_ERROR);
        }
        Guanliyuan login = guanliyuanService.login(guanliyuan);
        // 注释掉的代码用于检查管理员状态
//        if(!login.getStatus()){
//            return Result.error("1001","状态限制，无法登录系统");
//        }
        if(login != null) {
            // 创建包含用户信息的HashMap
            HashMap hashMap = new HashMap();
            hashMap.put("user", login);
            // 构建包含用户ID的Map，用于生成token
            Map<String, Object> map = MapWrapperUtils.builder(MapWrapperUtils.KEY_USER_ID,guanliyuan.getId());
            // 生成JWT token
            String token = JwtUtil.creatToken(map);
            hashMap.put("token", token);
            return Result.success(hashMap);
        }else {
            return Result.error();
        }
    }
    /**
     * 修改管理员密码
     * @param info 管理员对象，包含ID、原密码和新密码
     * @param request HTTP请求对象
     * @return 返回操作结果
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Guanliyuan info, HttpServletRequest request) {
        // 根据ID查询管理员信息
        Guanliyuan guanliyuan = guanliyuanService.findById(info.getId());
        // 对原密码进行MD5加密
        String oldPassword = SecureUtil.md5(info.getMima());
        // 验证原密码是否正确
        if (!oldPassword.equals(guanliyuan.getMima())) {
            return Result.error(ResultCode.PARAM_PASSWORD_ERROR.code, ResultCode.PARAM_PASSWORD_ERROR.msg);
        }
        // 对新密码进行MD5加密并更新
        info.setMima(SecureUtil.md5(info.getNewPassword()));
        Guanliyuan guanliyuan1 = new Guanliyuan();
        // 复制属性
        BeanUtils.copyProperties(info, guanliyuan1);
        // 更新管理员信息
        guanliyuanService.update(guanliyuan1);
        return Result.success();
    }
}
