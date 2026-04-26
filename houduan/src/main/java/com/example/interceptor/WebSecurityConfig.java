package com.example.interceptor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web安全配置类，实现了WebMvcConfigurer接口，用于配置Spring MVC的拦截器
 * 该类主要负责注册自定义的拦截器，并配置拦截器的URL模式
 */
@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
    /**
     * 创建并返回自定义的拦截器实例
     * @return 返回MyInterceptor对象，该对象将用于拦截和处理请求
     */
    @Bean
    public MyInterceptor getMyInterceptor() {
        return new MyInterceptor();
    }

    /**
     * 重写addInterceptors方法，配置拦截器注册
     * @param InterceptorRegistry 拦截器注册对象，用于添加和配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 注册自定义拦截器，并配置拦截路径和排除路径
        registry.addInterceptor(getMyInterceptor()).addPathPatterns("/**")  // 拦截所有路径

                // 排除登录相关路径，允许用户直接访问
                .excludePathPatterns("/login")  // 排除普通用户登录路径
                .excludePathPatterns("/guanliyuan/login")  // 排除管理员登录路径
                .excludePathPatterns("/yonghuxinxi/login")  // 排除用户信息登录路径
                .excludePathPatterns("/yonghuxinxi/register")  // 排除用户注册路径
                


                // 排除文件相关路径，允许直接下载和访问前端文件
                .excludePathPatterns("/files/download/**")  // 排除文件下载路径
				.excludePathPatterns("/files/front/**")  // 排除前端文件访问路径
                .excludePathPatterns("/upload/**")  // 排除文件上传路径
				.excludePathPatterns("/front/**")  // 排除前端资源路径
                .excludePathPatterns("/logout");  // 排除登出路径
    }
}
