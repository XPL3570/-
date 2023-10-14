package com.confession.globalConfig.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

//@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> limitScript;

    @Autowired
    public RateLimitInterceptor(RedisTemplate<String, Object> redisTemplate, DefaultRedisScript<Long> limitScript) {
        this.redisTemplate = redisTemplate;
        this.limitScript = limitScript;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = getClientIp(request);
        String key = "rate_limit:" + ip;
        Long count = redisTemplate.execute(limitScript, Collections.singletonList(key), 5, 1); // 设置每秒最多5个请求的限制

        if (count != null && count > 8) {
            response.setStatus(702);
            return false; // 如果超过限制，返回请求拒绝
        }

        return true; // 请求通过限流检查
    }

    private String getClientIp(HttpServletRequest request) {
        // 获取客户端IP地址的方法，根据实际情况进行调整
        return request.getRemoteAddr();
    }
}
