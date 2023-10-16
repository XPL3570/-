package com.confession.dto;

import lombok.Data;

@Data
public class ImageUploadKeyDTO {
    //访问keyId
    String accessKeyId;
    //临时秘钥签名
    String signature;
    //安全token
    String securityToken;
    //具体访问节点地址
    String host;
    //文件存放的相对路劲
    String key;
    //转码之后的权限标识
    String policyBase64Str;
    //访问的sts服务节点
    String endpoint;
}
