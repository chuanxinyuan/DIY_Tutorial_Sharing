package com.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Ershouwupin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ErshouwupinDao extends BaseMapper<Ershouwupin> {

@Select("select * from ershouwupin")
List<Map<String, Object>> daochuexcel();





@Select("SELECT distinct(wupinleixing) as aa,count(id) as bb FROM ershouwupin group by wupinleixing order by id")
List<Map<String, Object>> ershouwupin_tj_wupinleixing();




}
