package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Bianhuantu;
import com.example.service.BianhuantuService;
import com.example.vo.BianhuantuVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 变换图控制器类
 * 提供变换图的增删改查、导出Excel等功能的RESTful API接口
 */
@RestController
@RequestMapping(value = "/bianhuantu")
public class BianhuantuController {

    @Resource
    private BianhuantuService bianhuantuService; // 注入变换图服务

    /**
     * 添加变换图
     * @param bianhuantu 变换图视图对象，包含要添加的变换图信息
     * @return 返回操作结果，包含已添加的变换图信息
     */
    @PostMapping
    public Result<Bianhuantu> add(@RequestBody BianhuantuVo bianhuantu) {
        bianhuantuService.add(bianhuantu);
           return Result.success(bianhuantu);
    }
	
	
    /**
     * 导出Excel表格
     * @param response HTTP响应对象，用于输出Excel文件
     * @throws IOException 可能抛出的IO异常
     */
    @GetMapping("/getExcel")
    public void getExcel(HttpServletResponse response) throws IOException {
        // 1. 生成excel表头
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("mingcheng","名称"); // 设置名称列
        

        row.put("addtime","添加时间"); // 设置添加时间列
        List<Map<String, Object>> list = CollUtil.newArrayList(row);
        List<Map<String, Object>> daochuexcellist = bianhuantuService.daochuexcel(); // 获取要导出的Excel数据
        for (Map<String, Object> map : daochuexcellist) {
            list.add(map); // 将数据添加到列表中
        }
        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=chaoba.xlsx");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }
	
	

    @PostMapping("/deleteList")
    public Result<Bianhuantu> deleteList(@RequestBody BianhuantuVo bianhuantu) {
        bianhuantuService.deleteList(bianhuantu.getList());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        bianhuantuService.delete(id);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody BianhuantuVo bianhuantu) {
        bianhuantuService.update(bianhuantu);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Bianhuantu> detail(@PathVariable Integer id) {
        Bianhuantu bianhuantu = bianhuantuService.findById(id);
        return Result.success(bianhuantu);
    }

    @GetMapping
    public Result<List<Bianhuantu>> all() {
        return Result.success(bianhuantuService.list());
    }

    @PostMapping("/page")
    public Result<BianhuantuVo> page(@RequestBody BianhuantuVo bianhuantuVo) {
        return Result.success(bianhuantuService.findPage(bianhuantuVo));
    }
	//youupdt2login
}
