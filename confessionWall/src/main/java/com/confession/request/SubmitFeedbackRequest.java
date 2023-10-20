package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SubmitFeedbackRequest {
    @NotBlank(message = "反馈信息不能是空")
    @Size(max = 499, message = "反馈信息过长哦")
    private String massage;
}
