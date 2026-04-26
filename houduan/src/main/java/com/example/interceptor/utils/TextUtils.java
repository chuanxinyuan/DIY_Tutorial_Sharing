package com.example.interceptor.utils;

/**
 * TextUtils工具类，提供文本相关的实用方法
 */
public class TextUtils {
    /**
     * 检查字符串是否为空
     *
     * @param s 需要检查的字符串
     * @return 如果字符串为null或去除首尾空格后长度为0，则返回true；否则返回false
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
