package com.confession.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DeleteSubmissionRequest {

    @NotNull(message = "投稿id不能是空")
    @Min(value = 0,message = "不能小于0")
    private Integer postId;

    @NotNull(message = "墙id不能是空")
    @Min(value = 0,message = "不能小于0")
    private Integer wallId;

}
