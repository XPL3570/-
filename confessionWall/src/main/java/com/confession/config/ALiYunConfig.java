package com.confession.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun")
@Data
@ToString
public class ALiYunConfig {
    private String endpoint;
    //注意这两个id和秘钥设置在环境变量里面，详细查看阿里云文档，  客户端直传
//    private String keyId;
//    private String keySecret;
    private String bucketName;
    private String ramRoleArn;

    private String endpointNode;
}
