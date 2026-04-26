package com.example.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.ResultCode;
import com.example.dao.*;
import com.example.entity.Ershouwupin;
import com.example.exception.CustomException;
import com.example.service.ErshouwupinService;
import com.example.vo.ErshouwupinVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service("ershouwupinService")
public class ErshouwupinServiceImpl extends ServiceImpl<ErshouwupinDao, Ershouwupin> implements ErshouwupinService {

    @Resource
    private ErshouwupinDao ershouwupinDao;

    public Ershouwupin add(Ershouwupin ershouwupin) {
		
	   //youquchong1
        ershouwupin.setAddtime(new Date());
        ershouwupinDao.insert(ershouwupin);
        return ershouwupin;
    }
	
	@Override    public List<Map<String,Object>> ershouwupin_tj_wupinleixing() {    List<Map<String,Object>> returnMap = ershouwupinDao.ershouwupin_tj_wupinleixing();    return returnMap;    }    
	
    public void delete(Long id) {
        ershouwupinDao.deleteById(id);
    }
	

    public void update(Ershouwupin ershouwupin) {
        //youquchong2
        ershouwupinDao.updateById(ershouwupin);
    }
	
	@Override
    public List<Map<String, Object>> daochuexcel() {
        return ershouwupinDao.daochuexcel();
    }

    @Override
    public Ershouwupin findById(Integer id) {
        return ershouwupinDao.selectById(id);
    }

    public ErshouwupinVo findPage(ErshouwupinVo ershouwupinVo) {
        LambdaQueryWrapper<Ershouwupin> wrapper = Wrappers.lambdaQuery();
		wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getWupinbianhao()), Ershouwupin::getWupinbianhao,ershouwupinVo.getWupinbianhao());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getWupinmingcheng()), Ershouwupin::getWupinmingcheng,ershouwupinVo.getWupinmingcheng());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getWupinleixing()), Ershouwupin::getWupinleixing,ershouwupinVo.getWupinleixing());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getMiaoshu()), Ershouwupin::getMiaoshu,ershouwupinVo.getMiaoshu());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getJiage()), Ershouwupin::getJiage,ershouwupinVo.getJiage());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getTupian()), Ershouwupin::getTupian,ershouwupinVo.getTupian());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getFaburen()), Ershouwupin::getFaburen,ershouwupinVo.getFaburen());        wrapper.like(ObjectUtils.isNotEmpty(ershouwupinVo.getBeizhu()), Ershouwupin::getBeizhu,ershouwupinVo.getBeizhu());        wrapper.eq(ObjectUtils.isNotEmpty(ershouwupinVo.getIssh()), Ershouwupin::getIssh,ershouwupinVo.getIssh());        
        Page<Ershouwupin> page = new Page<>(ershouwupinVo.getCurrent(),ershouwupinVo.getCurrentNum());
        ershouwupinDao.selectPage(page,wrapper);
        List<Ershouwupin> ershouwupins = page.getRecords();
        ershouwupinVo.setList(ershouwupins);
        ershouwupinVo.setPages(page.getPages());
        ershouwupinVo.setTotal(page.getTotal());
        ershouwupinVo.setCurrent(page.getCurrent());
        ershouwupinVo.setCurrentNum(page.getSize());
        return ershouwupinVo;
    }

    @Override
    public void deleteList(List<Ershouwupin> list) {
        List<Integer> ids =  list.stream().map(Ershouwupin::getId).collect(Collectors.toList());
        ershouwupinDao.deleteBatchIds(ids);
    }
	
	
	
	
	
	//youxxianxci
}
