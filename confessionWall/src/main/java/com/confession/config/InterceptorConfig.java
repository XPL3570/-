package com.confession.config;

import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.globalConfig.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    private final RedisTemplate redisTemplate;


    @Autowired
    public InterceptorConfig(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        DefaultRedisScript<Long> limitScript = new DefaultRedisScript<>();
        limitScript.setScriptText(limitScriptText());
        limitScript.setResultType(Long.class);

        return new RateLimitInterceptor(redisTemplate, limitScript);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:e:\\表白墙项目图片上传地址\\") // 设置图片上传路径
                //当客户端首次请求一个静态资源时，服务器会返回该资源，并在响应头中添加Cache-Control字段，
                // 使客户端将该资源保存在本地缓存中，并在接下来的365天内直接使用本地缓存，而无需再次从服务器获取该资源。
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor()).addPathPatterns("/**"); //限流过滤器


        // 添加拦截器并指定拦截的路径规则
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/user/login") // 排除登录请求
                .excludePathPatterns("/admin/login") // 排除登录请求
//                .excludePathPatterns("/uploadImage") // 排除上传图片限制
                .excludePathPatterns("/api/user/register")
//                .excludePathPatterns("/api/confessionPost/readConfessionWall") // 测试加的 要删
                .excludePathPatterns("/upload/**"); // 排除获取图片信息
        ;
    }


    private String limitScriptText() {
        System.out.println("限流脚本初始化");
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key)\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current)\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current)";
    }


}
