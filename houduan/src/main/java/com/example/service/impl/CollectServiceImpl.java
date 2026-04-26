package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dao.CollectDao;
import com.example.dao.FileInfoDao;
import com.example.entity.Collect;
import com.example.entity.FileInfo;
import com.example.entity.Yonghuxinxi;
import com.example.entity.Ershouwupin;

import com.example.service.CollectService;
import com.example.service.YonghuxinxiService;
import com.example.service.ErshouwupinService;



import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现类
 * 继承ServiceImpl并实现CollectService接口，提供收藏相关的业务逻辑
 */
@Service("collectService")
public class CollectServiceImpl  extends ServiceImpl<CollectDao, Collect> implements CollectService {

    /**
     * 收藏数据访问对象
     */
    @Resource
    private CollectDao collectDao;
	
    /**
     * 用户信息服务对象
     */
	@Resource
private YonghuxinxiService yonghuxinxiService;
    /**
     * 二手物品服务对象
     */
@Resource
private ErshouwupinService ershouwupinService;

   

    /**
     * 添加收藏
     * @param collect 收藏对象
     * @return 如果收藏不存在则添加并返回收藏对象，否则返回null
     */
    public Collect add(Collect collect) {
        Collect collect1 = collectDao.findByUser(collect);
        if(collect1 == null) {
            collectDao.insert(collect);
            return collect;
        }else {
            return null;
        }
    }

    /**
     * 根据ID删除收藏
     * @param id 收藏ID
     */
    public void delete(Integer id) {
        collectDao.deleteById(id);
    }

    /**
     * 更新收藏信息
     * @param collect 收藏对象
     */
    public void update(Collect collect) {
        collectDao.updateById(collect);
    }

    /**
     * 根据ID查找收藏
     * @param id 收藏ID
     * @return 收藏对象
     */
    public Collect findById(Long id) {
        return collectDao.selectById(id);
    }

    /**
     * 查找用户的前台页面收藏列表
     * @param userId 用户ID
     * @return 收藏列表，每个收藏都包含用户和商品信息
     */
    public List<Collect> findFrontPages(Integer userId) {
        List<Collect> collects;
        if (userId != null){
            collects = collectDao.findByEndUserId(userId);
        } else {
            collects = new ArrayList<>();
        }
        for (Collect collect : collects) {
            packOrder(collect);
        }
        return collects;
    }
    /**
     * 包装收藏的用户和商品信息
 * 该方法用于将用户信息和商品信息封装到Collect对象中
     */

    private void packOrder(Collect collect) {
    // 根据用户ID查询用户信息并设置到Collect对象中
        collect.setYonghuxinxi(yonghuxinxiService.findById(collect.getUserId()));
    // 创建一个新的商品列表
        List<Ershouwupin> goodsList = CollUtil.newArrayList();
        collect.setProductList(goodsList);
        Ershouwupin goodsDetail = ershouwupinService.findById(collect.getShangpinxinxiID());
        if (goodsDetail != null) {
            goodsList.add(goodsDetail);
        }
    }
    /**
     * 根据用户ID查找用户信息
     * @param id 用户ID
     * @return 用户信息列表
     */
	public List<Yonghuxinxi> findPaperByYonghuxinxi(Integer id) {
        List<Collect> collects = collectDao.findByEndUserId(id);
        List<Yonghuxinxi> yonghuxinxiList = new ArrayList<>();
        for (Collect collect : collects) {
            Integer foreignId = collect.getShangpinxinxiID();
            Yonghuxinxi yonghuxinxi = null;
            if(foreignId != null){
                yonghuxinxi = yonghuxinxiService.findById(foreignId);
            };
            if(yonghuxinxi != null){
                yonghuxinxiList.add(yonghuxinxi);
            }
        }
        return yonghuxinxiList;
    }


	@Override
    public void deleteByUserId(Integer id, Integer userId) {
        collectDao.deleteByUserId(id,userId);
    }
    
}
