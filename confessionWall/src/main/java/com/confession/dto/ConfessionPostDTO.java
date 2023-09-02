package com.confession.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfessionPostDTO {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer wallId;
    private Integer userId;
    private String title;
    private String textContent;
    private String imageURL;
    private LocalDateTime createTime;
    private LocalDateTime publishTime;
    private Integer postStatus;

}
