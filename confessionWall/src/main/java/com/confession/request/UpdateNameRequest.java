package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateNameRequest {
    @NotBlank(message = "用户名不能是null")
    @Size(min = 2, max = 8, message = "用户名长度必须在2到8个字符之间")
    private String username;
}
