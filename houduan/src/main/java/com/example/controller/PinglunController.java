package com.example.controller;

import com.example.common.Result;
import com.example.entity.Pinglun;
import com.example.service.PinglunService;
import com.example.vo.PinglunVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论控制器
 * 提供评论相关的RESTful API接口
 */
@RestController
@RequestMapping(value = "/pinglun")
public class PinglunController {

    /**
     * 注入评论服务接口
     */
    @Resource
    private PinglunService pinglunService;

    /**
     * 添加评论
     * @param pinglun 评论视图对象
     * @return 返回操作结果和添加的评论数据
     */
    @PostMapping
    public Result<Pinglun> add(@RequestBody PinglunVo pinglun) {
        pinglunService.add(pinglun);
           return Result.success(pinglun);
    }
	
	

    /**
     * 批量删除评论
     * @param pinglun 包含评论列表的视图对象
     * @return 返回操作结果
     */
    @PostMapping("/deleteList")
    public Result<Pinglun> deleteList(@RequestBody PinglunVo pinglun) {
        pinglunService.deleteList(pinglun.getList());
        return Result.success();
    }

    /**
     * 根据ID删除评论
     * @param 评论ID
     * @return 返回操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        pinglunService.delete(id);
        return Result.success();
    }

    /**
     * 更新评论信息
     * @param pinglun 评论视图对象
     * @return 返回操作结果
     */
    @PutMapping
    public Result update(@RequestBody PinglunVo pinglun) {
        pinglunService.update(pinglun);
        return Result.success();
    }

    /**
     * 根据ID获取评论详情
     * @param id 评论ID
     * @return 返回操作结果和评论详情
     */
    @GetMapping("/{id}")
    public Result<Pinglun> detail(@PathVariable Integer id) {
        Pinglun pinglun = pinglunService.findById(id);
        return Result.success(pinglun);
    }

    /**
     * 获取所有评论列表
     * @return 返回操作结果和评论列表
     */
    @GetMapping
    public Result<List<Pinglun>> all() {
        return Result.success(pinglunService.list());
    }

    /**
     * 分页查询评论
     * @param pinglunVo 包含分页参数的评论视图对象
     * @return 返回操作结果和分页数据
     */
    @PostMapping("/page")
    public Result<PinglunVo> page(@RequestBody PinglunVo pinglunVo) {
        return Result.success(pinglunService.findPage(pinglunVo));
    }
	//youupdt2login
}
