package com.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Dingdan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * DingdanDao 数据访问接口
 * 继承 BaseMapper 获得基础的数据库操作方法
 * 使用 @Mapper 注解标记为 MyBatis 的 Mapper 接口
 */
@Mapper
public interface DingdanDao extends BaseMapper<Dingdan> {

    /**
     * 查询所有订单数据
     * @return 返回包含所有订单数据的 Map 列表
     */
@Select("select * from dingdan")
List<Map<String, Object>> daochuexcel();

    /**
     * 统计不同物品类型的订单数量
     * 按 wupinleixing 字段分组，计算每种类型的订单数量
     * @return 返回包含物品类型和对应数量的 Map 列表
     */
@Select("SELECT distinct(wupinleixing) as aa,count(id) as bb FROM dingdan group by wupinleixing order by id")
List<Map<String, Object>> dingdan_tj_wupinleixing();

}
