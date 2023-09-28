package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserNameModRequest {
    @NotNull(message = "用户id不能是null")
    private Integer userId;

    @NotBlank(message = "用户名不能是null")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
    private String username;
}
