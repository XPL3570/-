package com.confession.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserManageDTO {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String schoolName;

    private String openId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String wXAccount;

    private Integer gender;

    private String avatarURL;

    private Integer status;

}
