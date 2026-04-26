package com.example.controller;

import com.example.common.Result;
import com.example.entity.Xinwentongzhi;
import com.example.service.XinwentongzhiService;
import com.example.vo.XinwentongzhiVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 新闻通知控制器类
 * 处理与新闻通知相关的HTTP请求
 */
@RestController
@RequestMapping(value = "/xinwentongzhi")
public class XinwentongzhiController {

    @Resource
    private XinwentongzhiService xinwentongzhiService; // 注入新闻通知服务接口

    /**
     * 添加新闻通知
     * @param xinwentongzhi 新闻通知视图对象
     * @return 返回操作结果，包含添加的新闻通知信息
     */
    @PostMapping
    public Result<Xinwentongzhi> add(@RequestBody XinwentongzhiVo xinwentongzhi) {
        xinwentongzhiService.add(xinwentongzhi);
           return Result.success(xinwentongzhi);
    }
	
	

    /**
     * 批量删除新闻通知
     * @param xinwentongzhi 包含要删除的新闻通知列表的视图对象
     * @return 返回操作结果
     */
    @PostMapping("/deleteList")
    public Result<Xinwentongzhi> deleteList(@RequestBody XinwentongzhiVo xinwentongzhi) {
        xinwentongzhiService.deleteList(xinwentongzhi.getList());
        return Result.success();
    }

    /**
     * 根据ID删除新闻通知
     * @param id 新闻通知ID
     * @return 返回操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        xinwentongzhiService.delete(id);
        return Result.success();
    }

    /**
     * 更新新闻通知
     * @param xinwentongzhi 新闻通知视图对象
     * @return 返回操作结果
     */
    @PutMapping
    public Result update(@RequestBody XinwentongzhiVo xinwentongzhi) {
        xinwentongzhiService.update(xinwentongzhi);
        return Result.success();
    }

    /**
     * 根据ID获取新闻通知详情
     * @param id 新闻通知ID
     * @return 返回操作结果，包含新闻通知详情
     */
    @GetMapping("/{id}")
    public Result<Xinwentongzhi> detail(@PathVariable Integer id) {
        Xinwentongzhi xinwentongzhi = xinwentongzhiService.findById(id);
        return Result.success(xinwentongzhi);
    }

    /**
     * 获取所有新闻通知
     * @return 返回操作结果，包含所有新闻通知列表
     */
    @GetMapping
    public Result<List<Xinwentongzhi>> all() {
        return Result.success(xinwentongzhiService.list());
    }

    /**
     * 分页查询新闻通知
     * @param xinwentongzhiVo 包含分页查询条件的视图对象
     * @return 返回操作结果，包含分页查询结果
     */
    @PostMapping("/page")
    public Result<XinwentongzhiVo> page(@RequestBody XinwentongzhiVo xinwentongzhiVo) {
        return Result.success(xinwentongzhiService.findPage(xinwentongzhiVo));
    }
	//youupdt2login
}
