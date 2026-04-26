package com.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Dingdan;
import com.example.vo.DingdanVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface DingdanService extends IService<Dingdan> {
    Dingdan add(Dingdan dingdan);
    void delete(Long id);
    void update(Dingdan dingdan);
    Dingdan findById(Integer id);
    DingdanVo findPage(DingdanVo dingdanVo);
	
    void deleteList(List<Dingdan> list);
	List<Map<String,Object>> dingdan_tj_wupinleixing();    
	
	List<Map<String, Object>> daochuexcel();
	//youxxianxci
}
