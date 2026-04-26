package com.example.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.ResultCode;
import com.example.dao.*;
import com.example.entity.Bianhuantu;
import com.example.exception.CustomException;
import com.example.service.BianhuantuService;
import com.example.vo.BianhuantuVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * 变换图服务实现类
 * 继承ServiceImpl并实现BianhuantuService接口，提供变换图相关的业务逻辑实现
 */
@Service("bianhuantuService")
public class BianhuantuServiceImpl extends ServiceImpl<BianhuantuDao, Bianhuantu> implements BianhuantuService {

    /**
     * 注入变换图数据访问层
     */
    @Resource
    private BianhuantuDao bianhuantuDao;

    /**
     * 添加变换图
     * @param bianhuantu 变换图实体对象
     * @return 返回添加后的变换图实体对象
     */
    public Bianhuantu add(Bianhuantu bianhuantu) {
		
	   //youquchong1
        bianhuantu.setAddtime(new Date());
        bianhuantuDao.insert(bianhuantu);
        return bianhuantu;
    }
	
	
	
    public void delete(Long id) {
        bianhuantuDao.deleteById(id);
    }
	

    public void update(Bianhuantu bianhuantu) {
        //youquchong2
        bianhuantuDao.updateById(bianhuantu);
    }
	
/**
 * 导出Excel数据的方法
 * 该方法重写了父类的导出Excel功能，通过调用数据访问层的方法获取需要导出的数据
 *
 * @return 返回一个包含导出数据的List集合，每个元素是一个Map对象，Map的key为String类型，value为Object类型
 *         这种结构可以灵活地表示不同类型的数据，适合Excel导出场景
 */
	@Override
    public List<Map<String, Object>> daochuexcel() {
    // 调用数据访问层的方法，执行Excel数据导出的具体逻辑
        return bianhuantuDao.daochuexcel();
    }

    @Override
    public Bianhuantu findById(Integer id) {
        return bianhuantuDao.selectById(id);
    }

    public BianhuantuVo findPage(BianhuantuVo bianhuantuVo) {
        LambdaQueryWrapper<Bianhuantu> wrapper = Wrappers.lambdaQuery();
		wrapper.like(ObjectUtils.isNotEmpty(bianhuantuVo.getMingcheng()), Bianhuantu::getMingcheng,bianhuantuVo.getMingcheng());
        wrapper.like(ObjectUtils.isNotEmpty(bianhuantuVo.getTupian()), Bianhuantu::getTupian,bianhuantuVo.getTupian());
        
        Page<Bianhuantu> page = new Page<>(bianhuantuVo.getCurrent(),bianhuantuVo.getCurrentNum());
        bianhuantuDao.selectPage(page,wrapper);
        List<Bianhuantu> bianhuantus = page.getRecords();
        bianhuantuVo.setList(bianhuantus);
        bianhuantuVo.setPages(page.getPages());
        bianhuantuVo.setTotal(page.getTotal());
        bianhuantuVo.setCurrent(page.getCurrent());
        bianhuantuVo.setCurrentNum(page.getSize());
        return bianhuantuVo;
    }

    @Override
    public void deleteList(List<Bianhuantu> list) {
        List<Integer> ids =  list.stream().map(Bianhuantu::getId).collect(Collectors.toList());
        bianhuantuDao.deleteBatchIds(ids);
    }
	
	
	
	
}
