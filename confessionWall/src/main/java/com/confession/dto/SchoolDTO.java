package com.confession.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SchoolDTO  implements Serializable {
    /**
     * 学校ID
     */
    public Integer id;

    /**
     * 学校名称
     */
    public String schoolName;

    /**
     * 学校头像URL
     */
    public String avatarURL;

    /**
     * 学校描述
     */
    public String description;

    /**
     * 创建者ID
     */
//    public Integer creatorId;

    /**
     * 创建者名字
     */
    public String creatorName;

    /**
     * 创建者微信号
     */
    public String creatorWeChat;

    /**
     * 创建者手机号
     */
    public String creatorPhone;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime createTime;

    /**
     * 轮播图片
     */
    public String[] carouselImages;

    /**
     * 首页提示语提示信息
     */
    public String prompt;

    /**
     * 审核状态
     */
    public Integer isVerified;

}
