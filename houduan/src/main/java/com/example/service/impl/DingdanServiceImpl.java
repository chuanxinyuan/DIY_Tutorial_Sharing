package com.example.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.ResultCode;
import com.example.dao.*;
import com.example.entity.Dingdan;
import com.example.exception.CustomException;
import com.example.service.DingdanService;
import com.example.vo.DingdanVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service("dingdanService")
public class DingdanServiceImpl extends ServiceImpl<DingdanDao, Dingdan> implements DingdanService {

    @Resource
    private DingdanDao dingdanDao;

    public Dingdan add(Dingdan dingdan) {
		
	   //youquchong1
        dingdan.setAddtime(new Date());
        dingdanDao.insert(dingdan);
        return dingdan;
    }
	
	@Override    public List<Map<String,Object>> dingdan_tj_wupinleixing() {    List<Map<String,Object>> returnMap = dingdanDao.dingdan_tj_wupinleixing();    return returnMap;    }    
	
    public void delete(Long id) {
        dingdanDao.deleteById(id);
    }
	

    public void update(Dingdan dingdan) {
        //youquchong2
        dingdanDao.updateById(dingdan);
    }
	
	@Override
    public List<Map<String, Object>> daochuexcel() {
        return dingdanDao.daochuexcel();
    }

    @Override
    public Dingdan findById(Integer id) {
        return dingdanDao.selectById(id);
    }

    public DingdanVo findPage(DingdanVo dingdanVo) {
        LambdaQueryWrapper<Dingdan> wrapper = Wrappers.lambdaQuery();
		wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getDingdanbianhao()), Dingdan::getDingdanbianhao,dingdanVo.getDingdanbianhao());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getWupinbianhao()), Dingdan::getWupinbianhao,dingdanVo.getWupinbianhao());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getWupinmingcheng()), Dingdan::getWupinmingcheng,dingdanVo.getWupinmingcheng());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getWupinleixing()), Dingdan::getWupinleixing,dingdanVo.getWupinleixing());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getJiage()), Dingdan::getJiage,dingdanVo.getJiage());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getFaburen()), Dingdan::getFaburen,dingdanVo.getFaburen());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getGoumairen()), Dingdan::getGoumairen,dingdanVo.getGoumairen());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getLianxidianhua()), Dingdan::getLianxidianhua,dingdanVo.getLianxidianhua());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getDizhi()), Dingdan::getDizhi,dingdanVo.getDizhi());        wrapper.like(ObjectUtils.isNotEmpty(dingdanVo.getBeizhu()), Dingdan::getBeizhu,dingdanVo.getBeizhu());        
        Page<Dingdan> page = new Page<>(dingdanVo.getCurrent(),dingdanVo.getCurrentNum());
        dingdanDao.selectPage(page,wrapper);
        List<Dingdan> dingdans = page.getRecords();
        dingdanVo.setList(dingdans);
        dingdanVo.setPages(page.getPages());
        dingdanVo.setTotal(page.getTotal());
        dingdanVo.setCurrent(page.getCurrent());
        dingdanVo.setCurrentNum(page.getSize());
        return dingdanVo;
    }

    @Override
    public void deleteList(List<Dingdan> list) {
        List<Integer> ids =  list.stream().map(Dingdan::getId).collect(Collectors.toList());
        dingdanDao.deleteBatchIds(ids);
    }
	
	
	
	
	
	//youxxianxci
}
