package com.confession.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReportRecordDTO {
    private Integer id;

    private Integer userId;

    //举报投稿id
    private Integer reportId;

    //投稿发布者id
    private Integer userIdForSubmissionPublisher;

    private LocalDateTime createTime;

    // 举报建议
    private String message;

    //发布标题
    private String title;

    //发布内容文字
    private String textContent;

    //发布内容图片URL
    private String imageURL;

}
