package com.confession.config;

import com.confession.globalConfig.interceptor.RateLimitInterceptor;
import com.confession.globalConfig.interceptor.XssFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {


    private final RedisTemplate redisTemplate;

    @Autowired
    public WebConfig(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor());
    }
    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        DefaultRedisScript<Long> limitScript = new DefaultRedisScript<>();
        limitScript.setScriptText(limitScriptText());
        limitScript.setResultType(Long.class);

        return new RateLimitInterceptor(redisTemplate, limitScript);
    }

    private String limitScriptText() {
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")  //todo 改成读取配置文件，配置多数据源
                .addResourceLocations("file:e:\\表白墙项目图片上传地址\\") // 设置图片上传路径
                //当客户端首次请求一个静态资源时，服务器会返回该资源，并在响应头中添加Cache-Control字段，
                // 使客户端将该资源保存在本地缓存中，并在接下来的365天内直接使用本地缓存，而无需再次从服务器获取该资源。
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS));
    }



    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); //允许任何域名
        corsConfiguration.addAllowedHeader("*"); //允许任何头
//        corsConfiguration.addAllowedMethod("*"); //允许任何方法
        corsConfiguration.addAllowedMethod(HttpMethod.GET.name()); // 只允许GET方法
        corsConfiguration.addAllowedMethod(HttpMethod.POST.name()); // 只允许POST方法
        return corsConfiguration;
    }

    //xss拦截器
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("xssFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); //注册
        return new CorsFilter(source);
    }
}
