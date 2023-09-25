package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConfessionPostRequest {

    @NotNull(message = "wallId不能为空")
    private Integer wallId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题长度不能超过30个字符")
    private String title;

    @NotBlank(message = "文本内容不能为空")
    @Size(max = 1000, message = "投稿内容 长度不能超过1000个字符")
    private String textContent;

//    @NotBlank(message = "图片URL不能为空")
    private String imageURL;

    @NotNull(message = "isAnonymous不能为空")
    private Integer isAnonymous;
}

