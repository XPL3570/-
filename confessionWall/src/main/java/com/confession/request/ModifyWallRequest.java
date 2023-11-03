package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyWallRequest {

    @NotNull(message = "学校id不能为空")
    private Integer wallId;

    @NotBlank(message = "表白墙名字不能是null")
    private String wallName;

    @NotBlank(message = "描述信息或者备注不能是null")
    private String description;

    @NotNull(message = "是否禁用不能是null")
    private Boolean status;

}
