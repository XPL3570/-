package com.confession.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SchoolModifyRequest {

    // 学校ID
    @NotNull(message = "学校id不能为空")
    private Integer id;

    @NotNull(message = "学校恋爱墙可抽奖次数不能为空")
    private Integer numberLuckyDraws;

    @NotNull(message = "学校恋爱墙可放入次数不能为空")
    private Integer numberPaperInputs;

    @NotBlank(message = "学校名称不能为空")
    @Size(min=4,max = 50, message = "学校名称的长度在4-50位之间")
    private String schoolName;

    // 轮播图地址
    private String carouselImages;

    @NotBlank(message = "提示语不能是null")
    @Size(max = 1000,message = "提示语最长255个字")
    private String prompt;

    /**
     * 审核状态，0表示未审核，1表示通过，2未通过
     * 这里没有让他把状态修改成不通过的用法
     */
    @NotNull(message = "IsVerified不能为空")
    @Min(value = 0, message = "IsVerified的值只能为0或1")
    @Max(value = 1, message = "IsVerified的值只能为0或1")
    private Integer isVerified;
}
