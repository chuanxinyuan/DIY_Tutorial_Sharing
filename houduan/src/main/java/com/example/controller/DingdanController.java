package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Dingdan;
import com.example.service.DingdanService;
import com.example.vo.DingdanVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 订单控制器
 * 提供订单相关的RESTful API接口
 */
@RestController
@RequestMapping(value = "/dingdan")
public class DingdanController {

    @Resource
    private DingdanService dingdanService;

    /**
     * 添加订单
     * @param dingdan 订单视图对象，包含订单信息
     * @return 返回操作结果，包含添加的订单信息
     */
        @PostMapping
    public Result<Dingdan> add(@RequestBody DingdanVo dingdan) {
        dingdanService.add(dingdan);
        return Result.success(dingdan);
    }
	
	
    /**
     * 导出订单Excel表格
     * @param response HTTP响应对象，用于输出Excel文件
     * @throws IOException 可能抛出IO异常
     */
    @GetMapping("/getExcel")
    public void getExcel(HttpServletResponse response) throws IOException {
        // 1. 生成excel表头
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("dingdanbianhao","订单编号");
        row.put("wupinbianhao","物品编号");
        row.put("wupinmingcheng","物品名称");
        row.put("wupinleixing","物品类型");
        row.put("jiage","价格");
        row.put("faburen","发布人");
        row.put("goumairen","购买人");
        row.put("lianxidianhua","联系电话");
        row.put("dizhi","地址");
        row.put("beizhu","备注");
        

        row.put("addtime","添加时间");
        // 创建数据列表并添加表头
        List<Map<String, Object>> list = CollUtil.newArrayList(row);
        // 从服务层获取要导出的订单数据
        List<Map<String, Object>> daochuexcellist = dingdanService.daochuexcel();
        // 将数据添加到列表中
        for (Map<String, Object> map : daochuexcellist) {
            list.add(map);
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
	





	@GetMapping("/dingdan_tj_wupinleixing")
    public Result dingdan_tj_wupinleixing() {
        return Result.success(dingdanService.dingdan_tj_wupinleixing());
    }
    

    @PostMapping("/deleteList")
    public Result<Dingdan> deleteList(@RequestBody DingdanVo dingdan) {
        dingdanService.deleteList(dingdan.getList());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        dingdanService.delete(id);
        return Result.success();
    }

        @PutMapping
    public Result update(@RequestBody DingdanVo dingdan) {
        dingdanService.update(dingdan);
        return Result.success();
    }

    @GetMapping
    public Result<List<Dingdan>> all() {
        return Result.success(dingdanService.list());
    }






        @PostMapping("/page")
    public Result<DingdanVo> page(@RequestBody DingdanVo dingdanVo) {
        return Result.success(dingdanService.findPage(dingdanVo));
    }
}
