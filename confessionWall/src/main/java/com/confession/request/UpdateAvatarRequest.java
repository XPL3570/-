package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateAvatarRequest {
    @NotBlank(message = "图片地址不能是空")
    private String avatarUrl;

}