package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterSchoolRequest {

    @NotNull(message = "用户id不能为空")
    private Integer userId;

    @NotBlank(message = "学校图标不能为空")
    @Size(max = 255, message = "学校头像地址过长")
    private String avatarURL;

    @NotBlank(message = "学校名称不能为空")
    @Size(min=4,max = 50, message = "学校名称的长度在4-50位之间")
    private String schoolName;

    @NotBlank(message = "学校描述不能为空")
    @Size(max = 1000, message = "学校描述的长度不能超过1000")
    private String description;

    @NotBlank(message = "微信号不能为空")
    @Size(min = 6, max = 20, message = "微信号的长度必须在6到20之间")
    private String wechatNumber;

    @NotBlank(message = "手机号不能为空")
    @Size(min = 11, max = 11, message = "手机号的长度必须是11")
    private String PhoneNumber;


}
