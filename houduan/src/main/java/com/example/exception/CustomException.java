package com.example.exception;

import com.example.common.ResultCode;

/**
 * 自定义异常类，继承自RuntimeException
 * 用于处理业务逻辑中的异常情况，可以携带错误代码和错误信息
 */
public class CustomException extends RuntimeException {
    // 错误代码
    private String code;
    // 错误信息
    private String msg;

    /**
     * 构造方法1：通过ResultCode枚举创建异常对象
     * @param resultCode 包含错误代码和错误信息的枚举
     */
    public CustomException(ResultCode resultCode) {
        this.code = resultCode.code;
        this.msg = resultCode.msg;
    }

    /**
     * 构造方法2：通过自定义的错误代码和错误信息创建异常对象
     * @param code 错误代码
     * @param msg 错误信息
     */
    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 获取错误代码
     * @return 错误代码字符串
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置错误代码
     * @param code 要设置的错误代码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取错误信息
     * @return 错误信息字符串
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置错误信息
     * @param msg 要设置的错误信息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
