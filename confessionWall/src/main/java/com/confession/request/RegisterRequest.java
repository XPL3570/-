package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterRequest {

    private String code;

    private String schoolName;
    //消息密文
    private String encryptedData;

    //加密算法的初始向量
    private String iv;

}
