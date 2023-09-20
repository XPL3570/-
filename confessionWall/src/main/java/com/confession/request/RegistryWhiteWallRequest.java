package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegistryWhiteWallRequest {

    @NotNull(message = "学校id不能为空")
    private Integer schoolId;

    @NotBlank(message = "表白墙头像图片地址不能是空")
    private String avatarURL;


    @NotBlank(message = "表白墙名字不能是null")
    @Size(min = 5, max = 20, message = "长度必须在2到8个字符之间")
    private String ConfessionWallName;

    @NotBlank(message = "表白墙描述不能是null")
    private String description;

}
