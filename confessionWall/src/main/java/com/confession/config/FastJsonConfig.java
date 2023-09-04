package com.confession.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastJsonConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setSupportedMediaTypes(getSupportedMediaTypes());
        converter.setFastJsonConfig(getFastJsonConfig());
        converters.add(converter);
    }

    private List<MediaType> getSupportedMediaTypes() {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        return supportedMediaTypes;
    }


    private com.alibaba.fastjson.support.config.FastJsonConfig getFastJsonConfig() {
        com.alibaba.fastjson.support.config.FastJsonConfig fastJsonConfig = new com.alibaba.fastjson.support.config.FastJsonConfig();

        // 创建 SerializeConfig 对象，用于配置全局序列化规则
        SerializeConfig serializeConfig = new SerializeConfig();

        // 指定日期的序列化格式
        String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormatSerializer serializer = new SimpleDateFormatSerializer(dateFormatPattern);

        // 将日期的序列化规则添加到 SerializeConfig 中
        serializeConfig.put(LocalDateTime.class, serializer);

        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonConfig.setDateFormat(dateFormatPattern);

        return fastJsonConfig;
    }
}

