package com.example.interceptor.utils;

import java.util.HashMap;

/**
 * MapWrapperUtils 类，继承自HashMap<String, Object>
 * 提供了更便捷的Map操作方法，支持链式调用
 */
public class MapWrapperUtils extends HashMap<String, Object> {
    // 定义静态常量，用户ID的键名
    public static String KEY_USER_ID = "userId";

    /**
     * 重写put方法，支持链式调用
     * @param key 键名
     * @param value 键值
     * @return 返回当前MapWrapperUtils实例，支持链式调用
     */
    @Override
    public MapWrapperUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 构建器方法，用于创建MapWrapperUtils实例
     * @param key 初始键名
     * @param value 初始键值
     * @return 返回创建并初始化后的MapWrapperUtils实例
     */
    public static MapWrapperUtils builder(String key, Object value) {
       MapWrapperUtils wrapperUtils = new MapWrapperUtils();
        wrapperUtils.put(key, value);
        return wrapperUtils;
    }
}
