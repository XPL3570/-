package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AdminModPwdRequest {
    @NotBlank(message = "原密码不能为空")
    @Size(min = 6,max = 20,message = "密码长度在6-20位之间")
    private String originalPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6,max = 20,message = "新密码长度在6-20位之间")
    private String newPassword;

    @NotBlank(message = "新密码确定不能为空")
    @Size(min = 6,max = 20,message = "新密码确认长度在6-20位之间")
    private String newPasswordConfirmation;

}
