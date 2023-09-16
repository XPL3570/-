package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterRequest {

    @NotBlank(message = "code不能为空")
    private String code;

    @NotBlank(message = "学校名称不能为空")
    private String schoolName;

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "头像URL不能为空")
    private String avatarUrl;
}
