package com.confession.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConfessionPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer wallId;
    private Integer userId;
    private UserDTO userInfo;
    private String title;
    private String textContent;
    private List<String> imageURL;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

    private Integer postStatus;
    private Boolean isAnonymous;

    private List<CommentDTO> mainComments;
    private List<CommentDTO> subComments;
}

