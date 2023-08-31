package com.confession.config;

import com.confession.globalConfig.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器并指定拦截的路径规则
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/user/login") // 排除登录请求
                .excludePathPatterns("/uploadImage") // 排除上传图片限制
                .excludePathPatterns("/api/user/register") // 排除注册请求
                .excludePathPatterns("/upload/**"); // 排除获取图片信息
    }
}
