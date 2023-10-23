package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserWeChatModRequest {
    @NotNull(message = "是否可以获取微信不能为空")
    private Boolean canObtainWeChat;

    @NotBlank(message = "微信账号不能为空")
    private String wxAccount;
}
