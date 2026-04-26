package com.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Ershouwupin;
import com.example.vo.ErshouwupinVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ErshouwupinService extends IService<Ershouwupin> {
    Ershouwupin add(Ershouwupin ershouwupin);
    void delete(Long id);
    void update(Ershouwupin ershouwupin);
    Ershouwupin findById(Integer id);
    ErshouwupinVo findPage(ErshouwupinVo ershouwupinVo);
	
    void deleteList(List<Ershouwupin> list);
	List<Map<String,Object>> ershouwupin_tj_wupinleixing();    
	
	List<Map<String, Object>> daochuexcel();
	//youxxianxci
}
