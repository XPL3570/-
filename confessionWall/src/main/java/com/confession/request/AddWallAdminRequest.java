package com.confession.request;
//添加表白墙管理员

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddWallAdminRequest {
    @NotNull(message = "学校id不能为空")
    private Integer schoolId;
    @NotNull(message = "表白墙id不能为空")
    private Integer wallId;
    @NotNull(message = "用户id不能为空")
    private Integer userId;

    private String phoneNum;

    private String weChatId;
}
