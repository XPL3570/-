package com.confession.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学校审核dto
 */
@Data
public class SchoolApplicationDTO {
    // 学校ID
    private Integer schoolId;

    // 学校名称
    private String schoolName;

    //创建时间
    private LocalDateTime createTime;

    // 头像地址
    private String avatarURL;

    // 描述内容
    private String description;

    // 微信号
    private String wechatNumber;

    // 手机号
    private String phoneNumber;




}

