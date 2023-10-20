package com.confession.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentDTO implements Serializable {

    private Integer id;
    //为了后续可以加页面点击跳转来用的
    private Integer confessionPostReviewId;

    private Integer parentCommentId;

    private Integer userId;

    private String commentContent;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime commentTime;

    private String userName;
    private String avatarURL;

}
