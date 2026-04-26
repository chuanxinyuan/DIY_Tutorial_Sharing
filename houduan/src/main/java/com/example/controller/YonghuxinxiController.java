package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.Yonghuxinxi;
import com.example.exception.CustomException;
import com.example.service.YonghuxinxiService;
import com.example.interceptor.utils.MapWrapperUtils;
import com.example.interceptor.utils.jwt.JwtUtil;
import com.example.vo.YonghuxinxiVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 用户信息控制器
 * 提供用户信息的增删改查、导出Excel、登录注册等功能接口
 */
@RestController
@RequestMapping(value = "/yonghuxinxi")
public class YonghuxinxiController {

    @Resource
    private YonghuxinxiService yonghuxinxiService; // 注入用户信息服务

    /**
     * 添加用户信息
     * @param yonghuxinxi 用户信息对象
     * @return 返回操作结果和添加的用户信息
     */
        @PostMapping
    public Result<Yonghuxinxi> add(@RequestBody YonghuxinxiVo yonghuxinxi) {
        yonghuxinxiService.add(yonghuxinxi); // 调用服务层添加用户
        return Result.success(yonghuxinxi); // 返回成功结果和用户信息
    }
	
	
    /**
     * 导出用户信息为Excel文件
     * @param response HTTP响应对象
     * @throws IOException 可能抛出的IO异常
     */
    @GetMapping("/getExcel")
    public void getExcel(HttpServletResponse response) throws IOException {
        // 1. 生成excel
        Map<String, Object> row = new LinkedHashMap<>(); // 创建有序Map存储Excel表头
        // 设置Excel表头字段
        row.put("yonghuming","用户名");
        row.put("xingming","姓名");
        row.put("xingbie","性别");
        row.put("shoujihao","手机号");
        row.put("beizhu","备注");
        

        row.put("addtime","添加时间");
        // 创建Excel表头数据列表
        List<Map<String, Object>> list = CollUtil.newArrayList(row);
        // 从数据库获取要导出的数据
        List<Map<String, Object>> daochuexcellist = yonghuxinxiService.daochuexcel();
        // 将数据添加到列表中
        for (Map<String, Object> map : daochuexcellist) {
            list.add(map);
        }
        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true); // 创建Excel写入器
        writer.write(list, true); // 写入数据到Excel
        // 设置响应类型和头信息，使浏览器下载Excel文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=chaoba.xlsx");
        // 输出Excel文件
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true); // 刷新输出流
        writer.close(); // 关闭写入器
        IoUtil.close(System.out); // 关闭输出流
    }
	
	

    /**
     * 批量删除用户信息
     * @param yonghuxinxi 包含要删除的用户ID列表的对象
     * @return 返回操作结果
     */
    @PostMapping("/deleteList")
    public Result<Yonghuxinxi> deleteList(@RequestBody YonghuxinxiVo yonghuxinxi) {
        yonghuxinxiService.deleteList(yonghuxinxi.getList()); // 调用服务层批量删除用户
        return Result.success(); // 返回成功结果
    }

    /**
     * 根据ID删除单个用户
     * @param id 用户ID
     * @return 返回操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        yonghuxinxiService.delete(id); // 调用服务层删除用户
        return Result.success(); // 返回成功结果
    }

    /**
     * 更新用户信息
     * @param yonghuxinxi 用户信息对象
     * @return 返回操作结果
     */
    @PutMapping
    public Result update(@RequestBody YonghuxinxiVo yonghuxinxi) {
        yonghuxinxiService.update(yonghuxinxi); // 调用服务层更新用户
        return Result.success(); // 返回成功结果
    }

    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 返回操作结果和用户信息
     */
    @GetMapping("/{id}")
    public Result<Yonghuxinxi> detail(@PathVariable Integer id) {
        Yonghuxinxi yonghuxinxi = yonghuxinxiService.findById(id); // 调用服务层查找用户
        return Result.success(yonghuxinxi); // 返回成功结果和用户信息
    }

    /**
     * 获取所有用户信息
     * @return 返回操作结果和用户列表
     */
    @GetMapping
    public Result<List<Yonghuxinxi>> all() {
        return Result.success(yonghuxinxiService.list()); // 返回成功结果和用户列表
    }

    /**
     * 分页查询用户信息
     * @param yonghuxinxiVo 分页查询条件
     * @return 返回操作结果和分页数据
     */
    @PostMapping("/page")
    public Result<YonghuxinxiVo> page(@RequestBody YonghuxinxiVo yonghuxinxiVo) {
        return Result.success(yonghuxinxiService.findPage(yonghuxinxiVo)); // 返回成功结果和分页数据
    }
    /**
     * 用户登录
     * @param yonghuxinxi 用户登录信息
     * @param request HTTP请求对象
     * @return 返回操作结果和登录信息（包含token）
     */
	    @PostMapping("/login")
    public Result login(@RequestBody Yonghuxinxi yonghuxinxi, HttpServletRequest request) {
        // 验证用户名和密码是否为空
        if (StrUtil.isBlank(yonghuxinxi.getYonghuming()) || StrUtil.isBlank(yonghuxinxi.getMima())) {
            throw new CustomException(ResultCode.PARAM_LOST_ERROR);
        }
        // 验证登录信息
        Yonghuxinxi login = yonghuxinxiService.login(yonghuxinxi);
//        if(!login.getStatus()){
//            return Result.error("1001","状态限制，无法登录系统");
//        }
        if(login != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("user", login);
            Map<String, Object> map = MapWrapperUtils.builder(MapWrapperUtils.KEY_USER_ID,yonghuxinxi.getId());
            String token = JwtUtil.creatToken(map);
            hashMap.put("token", token);
            return Result.success(hashMap);
        }else {
            return Result.error();
        }
    }
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Yonghuxinxi info, HttpServletRequest request) {
        Yonghuxinxi yonghuxinxi = yonghuxinxiService.findById(info.getId());
        String oldPassword = SecureUtil.md5(info.getMima());
        if (!oldPassword.equals(yonghuxinxi.getMima())) {
            return Result.error(ResultCode.PARAM_PASSWORD_ERROR.code, ResultCode.PARAM_PASSWORD_ERROR.msg);
        }
        info.setMima(SecureUtil.md5(info.getNewPassword()));
        Yonghuxinxi yonghuxinxi1 = new Yonghuxinxi();
        BeanUtils.copyProperties(info, yonghuxinxi1);
        yonghuxinxiService.update(yonghuxinxi1);
        return Result.success();
    }
	@PostMapping("/register")
    public Result<Yonghuxinxi> register(@RequestBody YonghuxinxiVo yonghuxinxi) {
        yonghuxinxiService.add(yonghuxinxi);
        return Result.success(yonghuxinxi);
    }
}
