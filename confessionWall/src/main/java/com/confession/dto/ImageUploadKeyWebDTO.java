package com.confession.dto;

import lombok.Data;

@Data
public class ImageUploadKeyWebDTO {
    //访问keyId
    String accessid;
    //临时秘钥签名
    String signature;
    //具体访问节点
    String host;
    //文件存放的相对路劲,对应的前端是key
    String dir;
    //base64转码之后的权限标识
    String policy;
    //由服务器端指定的Policy过期时间，格式为Unix时间戳（自UTC时间1970年01月01号开始的秒数）。
    String expire;
    //安全token
//    String securityToken;
}
