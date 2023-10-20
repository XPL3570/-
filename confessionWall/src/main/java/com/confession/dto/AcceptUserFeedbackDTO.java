package com.confession.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AcceptUserFeedbackDTO {

    private Integer id;

    private Integer userid;

    private Integer schoolId;

    private String userName;

    private String message;

    private Date createTime;

    private Boolean isRead;



}
