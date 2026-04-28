package com.example.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.example.common.Result;
import com.example.interceptor.utils.TextUtils;
import com.example.interceptor.utils.jwt.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义拦截器类，实现HandlerInterceptor接口，用于处理请求前的拦截逻辑
 */
@Component
public class MyInterceptor implements HandlerInterceptor {
    /**
     * 请求前处理方法，在请求处理之前进行调用
     * @param request 当前HTTP请求对象
     * @param resp 当前HTTP响应对象
     * @param handler 请求处理的方法对象
     * @return 如果返回true，继续流程；如果返回false，终端流程
     * @throws Exception 可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse resp, Object handler) throws Exception {
            // 设置响应编码为UTF-8
            resp.setCharacterEncoding("utf-8");
            // 设置响应内容类型为JSON，并指定编码为UTF-8
            resp.setContentType("application/json; charset=utf-8");
            // 从请求头中获取token
            String token = request.getHeader("token");
            // 检查token是否为空
            if (TextUtils.isEmpty(token)) {
                makeResponse(resp, Result.error("-1", "请携带token"));
                return false;
            }
                        Result returnData = JwtUtil.valid(token);
            // 令牌验证通过
            if ("0".equals(returnData.getCode())) {
                return true;
            }
            // 令牌过期或无效——检查有效载荷是否包含 ID（用于区分"登录过期"与"token有误"）
            Object dataObj = returnData.getData();
            if (dataObj instanceof Long) {
                Long id = (Long) dataObj;
                if (id == null) {
                    makeResponse(resp, Result.error("-1", "token有误"));
                    return false;
                }
                // ID 存在但代码不是 "0" => 过期
                makeResponse(resp, returnData);
                return false;
            }
            // 没有有效数据 => 无效令牌
            makeResponse(resp, Result.error("-1", "token有误"));
            return false;
    }
    private void makeResponse(HttpServletResponse resp, Result returnData) {
        try {
            resp.getWriter().print(JSONObject.toJSONString(returnData));
            resp.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
