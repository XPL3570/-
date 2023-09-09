package com.confession.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PostCommentRequest {
        private Integer confessionPostReviewId;
        private Integer parentCommentId;
        private String commentContent;
}
