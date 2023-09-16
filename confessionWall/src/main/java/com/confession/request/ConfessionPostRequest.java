package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConfessionPostRequest {

    @NotNull(message = "wallId不能为空")
    private Integer wallId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "文本内容不能为空")
    private String textContent;

    @NotBlank(message = "图片URL不能为空")
    private String imageURL;

    @NotNull(message = "isAnonymous不能为空")
    private Integer isAnonymous;
}

