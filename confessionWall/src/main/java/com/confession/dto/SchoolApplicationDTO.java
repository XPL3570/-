package com.confession.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学校审核dto
 */
@Data
public class SchoolApplicationDTO implements Serializable {

    //创建者用户名
    private String creatorUsername;


    //创建者头像地址url
    private String creatorUserAvatarURL;

    // 学校ID
    private Integer schoolId;

    // 学校名称
    private String schoolName;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 微信号
    private String wechatNumber;

    // 手机号
    private String phoneNumber;


}

