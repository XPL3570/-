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
    private String keyId;
    private String keySecret;
    private String bucketName;
    private String ramRoleArn;

    private String endpointNode;
}
