package com.confession.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class LotteryRequest {


    /**
     * 学校id
     */
    @NotNull(message = "学校id不能为空")
    private Integer schoolId;

    /**
     * 性别 0男   1女
     */
    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别必须是0或1")
    @Max(value = 1, message = "性别必须是0或1")
    private Integer gender;

    /**
     * 联系方式
     */
    @NotBlank(message = "联系方式不能为空")
    @Size(min = 6, message = "联系方式长度必须大于5个字符")
    private String contactInfo;

    /**
     * 介绍
     */
    @NotBlank(message = "介绍不能为空")
    @Size(min = 10, message = "介绍信息必须大于10个字符")
    private String introduction;

    /**
     * 图片地址
     */
    private String imageUrl;

}