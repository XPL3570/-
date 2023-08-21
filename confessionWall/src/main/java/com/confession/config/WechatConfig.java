package com.confession.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class WechatConfig {
    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

}
