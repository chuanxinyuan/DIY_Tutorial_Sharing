package com.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Yonghuxinxi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface YonghuxinxiDao extends BaseMapper<Yonghuxinxi> {

@Select("select * from yonghuxinxi")
List<Map<String, Object>> daochuexcel();


}
