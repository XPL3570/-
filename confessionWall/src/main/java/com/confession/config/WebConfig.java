package com.confession.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:e:\\表白墙项目图片上传地址\\") // 设置图片上传路径
                //当客户端首次请求一个静态资源时，服务器会返回该资源，并在响应头中添加Cache-Control字段，
                // 使客户端将该资源保存在本地缓存中，并在接下来的365天内直接使用本地缓存，而无需再次从服务器获取该资源。
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }
}
