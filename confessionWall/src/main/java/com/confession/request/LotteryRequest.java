package com.confession.request;

import lombok.Data;

@Data
public class LotteryRequest {


    /**
     * 学校id
     */
    private Integer schoolId;

    /**
     * 性别 0男   1女
     */
    private Integer gender;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 介绍
     */
    private String introduction;

    /**
     * 图片地址
     */
    private String imageUrl;


}