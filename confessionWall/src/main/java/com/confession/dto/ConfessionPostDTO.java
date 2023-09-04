package com.confession.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConfessionPostDTO {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer wallId;
    private Integer userId;
    private String title;
    private String textContent;
    private List<String> imageURL;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    private Integer postStatus;
}

