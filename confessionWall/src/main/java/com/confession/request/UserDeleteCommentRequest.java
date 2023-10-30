package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户删除自己评论
 * 请求包装类
 */
@Data
public class UserDeleteCommentRequest{
    @NotNull(message = "投稿id不能是null")
    private Integer postId;

    @NotNull(message = "评论id不能是null")
    private Integer commentId;
}
