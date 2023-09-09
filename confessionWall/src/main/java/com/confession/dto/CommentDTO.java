package com.confession.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Integer id;
    private Integer confessionPostReviewId;

    private Integer parentCommentId;

    private Integer userId;

    private String commentContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime commentTime;

    private String userName;
    private String avatarURL;

}
