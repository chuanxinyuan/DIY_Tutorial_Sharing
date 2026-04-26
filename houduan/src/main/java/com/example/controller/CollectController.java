package com.example.controller;

import com.example.common.Result;
import com.example.entity.Collect;
import com.example.service.impl.CollectServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 收藏控制器类，处理收藏相关的HTTP请求
 * 使用@RestController注解标记这是一个RESTful控制器
 * @RequestMapping("/collect")指定了该控制器的根路径
 */
@RestController
@RequestMapping(value = "/collect")
public class CollectController {
    // 使用@Resource注解注入CollectServiceImpl实例
    @Resource
    private CollectServiceImpl collectService;

    /**
     * 添加收藏的POST请求处理方法
     * @param collect 收藏信息，通过请求体传递
     * @return 返回操作结果，包含收藏信息或错误信息
     */
    @PostMapping
    public Result<Collect> add(@RequestBody Collect collect) {
        // 调用服务层添加收藏
        Collect collect1 =collectService.add(collect);
        // 如果添加结果为null，说明重复收藏
        if(collect1  == null){
            return Result.success("请勿重复收藏");
        }
        // 返回成功结果和收藏信息
        return Result.success(collect);
    }

    /**
     * 更新收藏的PUT请求处理方法
     * @param collect 更新的收藏信息，通过请求体传递
     * @return 返回操作结果
     */
    @PutMapping
    public Result update(@RequestBody Collect collect) {
        // 调用服务层更新收藏
        collectService.update(collect);
        // 返回成功结果
        return Result.success();
    }

    /**
     * 获取收藏详情的GET请求处理方法
     * @param id 收藏ID，通过路径变量传递
     * @return 返回操作结果，包含收藏详情信息
     */
    @GetMapping("/{id}")
    public Result<Collect> detail(@PathVariable Long id) {
        // 调用服务层查找收藏
        Collect collect = collectService.findById(id);
        // 返回成功结果和收藏信息
        return Result.success(collect);
    }
}
