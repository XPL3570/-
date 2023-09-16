package com.confession.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class PostCommentRequest {
        @NotNull(message = "内容id不能是null")
        private Integer confessionPostReviewId;

        private Integer parentCommentId;
        @NotBlank(message = "内容不能是空")
        private String commentContent;
}
