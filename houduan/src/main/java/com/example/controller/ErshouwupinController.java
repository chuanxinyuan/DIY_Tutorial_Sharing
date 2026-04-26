package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Ershouwupin;
import com.example.service.ErshouwupinService;
import com.example.vo.ErshouwupinVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 二手物品控制器类
 * 处理二手物品相关的HTTP请求
 */
@RestController
@RequestMapping(value = "/ershouwupin")
public class ErshouwupinController {

    @Resource
    private ErshouwupinService ershouwupinService; // 注入二手物品服务

    /**
     * 添加二手物品
     * @param ershouwupin 二手物品视图对象
     * @return 返回操作结果，包含添加的二手物品信息
     */
        @PostMapping
    public Result<Ershouwupin> add(@RequestBody ErshouwupinVo ershouwupin) {
        ershouwupinService.add(ershouwupin);
        return Result.success(ershouwupin);
    }
	
	
    /**
     * 导出Excel表格
     * @param response HTTP响应对象
     * @throws IOException 可能抛出的IO异常
     */
    @GetMapping("/getExcel")
    public void getExcel(HttpServletResponse response) throws IOException {
        // 1. 生成excel表头
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("wupinbianhao","物品编号");
        row.put("wupinmingcheng","物品名称");
        row.put("wupinleixing","物品类型");
        row.put("miaoshu","描述");
        row.put("jiage","价格");
        row.put("faburen","发布人");
        row.put("beizhu","备注");
        

        row.put("addtime","添加时间");
        // 2. 获取导出数据并合并表头和数据
        List<Map<String, Object>> list = CollUtil.newArrayList(row);
        List<Map<String, Object>> daochuexcellist = ershouwupinService.daochuexcel();
        for (Map<String, Object> map : daochuexcellist) {
            list.add(map);
        }
        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
    }
	





	@GetMapping("/ershouwupin_tj_wupinleixing")
    public Result ershouwupin_tj_wupinleixing() {
        return Result.success(ershouwupinService.ershouwupin_tj_wupinleixing());
    }
    

    @PostMapping("/deleteList")
    public Result<Ershouwupin> deleteList(@RequestBody ErshouwupinVo ershouwupin) {
        ershouwupinService.deleteList(ershouwupin.getList());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        ershouwupinService.delete(id);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody ErshouwupinVo ershouwupin) {
        ershouwupinService.update(ershouwupin);
        return Result.success();
    }


    @GetMapping
    public Result<List<Ershouwupin>> all() {
        return Result.success(ershouwupinService.list());
    }






        @PostMapping("/page")
    public Result<ErshouwupinVo> page(@RequestBody ErshouwupinVo ershouwupinVo) {
        return Result.success(ershouwupinService.findPage(ershouwupinVo));
    }
}
