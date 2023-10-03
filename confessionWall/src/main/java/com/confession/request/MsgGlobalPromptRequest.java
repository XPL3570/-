package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MsgGlobalPromptRequest {


    @NotBlank(message = "提示语不能为空")
    private String message;

    /**
     * 0 表示关闭 1表示开启
     */
    @NotNull(message = "开关不能为空")
    private Boolean mainSwitch;
}
