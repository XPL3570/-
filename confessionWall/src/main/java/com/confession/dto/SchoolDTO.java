package com.confession.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SchoolDTO  implements Serializable {
    /**
     * 学校ID
     */
    private Integer id;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 创建者ID
     */
//    public Integer creatorId;

    /**
     * 创建者名字
     */
    private String creatorName;

    /**
     * 创建者微信号
     */
    private String creatorWeChat;

    /**
     * 创建者手机号
     */
    private String creatorPhone;

    /**
     * 创建时间
     */
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    /**
     * 轮播图片
     */
    private String[] carouselImages;

    /**
     * 首页提示语提示信息
     */
    private String prompt;

    /**
     * 审核状态
     */
    private Integer isVerified;

    /**
     * 学校可抽奖数，也要看服务器的策略服务器
     */
    private Integer numberLuckyDraws;

    /**
     * 学校纸条可放入次数，也看服务器设置的策略
     */
    private Integer numberPaperInputs;

}
