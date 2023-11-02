package com.confession.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//获取其他用户联系方式请求包装类
@Data
public class ObtainContactInfoRequest {
    @NotNull(message = "要添加的用户id不能是null")
    @Min(value = 0,message = "不能小于0")
    private Integer receiverId;

    @NotBlank(message = "申请理由不能是null")
    @Size(max = 20,min = 4,message = "申请理由在4-20个字哦！")
    private String applicationReason;
}
