package com.example.controller;

import com.example.common.Result;
import com.example.entity.Liuyanban;
import com.example.service.LiuyanbanService;
import com.example.vo.LiuyanbanVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 留言板控制器类
 * 处理留言板相关的HTTP请求，包括增删改查等操作
 */
@RestController
@RequestMapping(value = "/liuyanban")
public class LiuyanbanController {

    /**
     * 注入留言板服务接口
     */
    @Resource
    private LiuyanbanService liuyanbanService;

    /**
     * 添加留言
     * @param liuyanban 留言信息对象
     * @return 返回操作结果，包含添加的留言信息
     */
    @PostMapping
    public Result<Liuyanban> add(@RequestBody LiuyanbanVo liuyanban) {
        liuyanbanService.add(liuyanban);
           return Result.success(liuyanban);
    }

    /**
     * 批量删除留言
     * @param liuyanban 包含要删除的留言ID列表的对象
     * @return 返回操作结果
     */
    @PostMapping("/deleteList")
    public Result<Liuyanban> deleteList(@RequestBody LiuyanbanVo liuyanban) {
        liuyanbanService.deleteList(liuyanban.getList());
        return Result.success();
    }

    /**
     * 根据ID删除留言
     * @param id 留言ID
     * @return 返回操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        liuyanbanService.delete(id);
        return Result.success();
    }

    /**
     * 更新留言信息
     * @param liuyanban 留言信息对象
     * @return 返回操作结果
     */
    @PutMapping
    public Result update(@RequestBody LiuyanbanVo liuyanban) {
        liuyanbanService.update(liuyanban);
        return Result.success();
    }

    /**
     * 根据ID获取留言详情
     * @param id 留言ID
     * @return 返回操作结果，包含留言详情信息
     */
    @GetMapping("/{id}")
    public Result<Liuyanban> detail(@PathVariable Integer id) {
        Liuyanban liuyanban = liuyanbanService.findById(id);
        return Result.success(liuyanban);
    }

    /**
     * 获取所有留言
     * @return 返回操作结果，包含所有留言列表
     */
    @GetMapping
    public Result<List<Liuyanban>> all() {
        return Result.success(liuyanbanService.list());
    }

    /**
     * 分页查询留言
     * @param liuyanbanVo 包含分页查询条件的对象
     * @return 返回操作结果，包含分页查询结果
     */
    @PostMapping("/page")
    public Result<LiuyanbanVo> page(@RequestBody LiuyanbanVo liuyanbanVo) {
        return Result.success(liuyanbanService.findPage(liuyanbanVo));
    }
	//youupdt2login
}
