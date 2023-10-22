package com.confession.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AcceptUserFeedbackDTO {

    private Integer id;

    private Integer userid;

    private String userName;

    private Integer userStatus;

    private Integer schoolId;

    private Integer score; //评分

    private String message;

    private LocalDateTime createTime;

    private Boolean isRead;



}
