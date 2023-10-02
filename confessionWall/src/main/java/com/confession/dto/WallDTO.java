package com.confession.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WallDTO {

    /**
     * 表白墙ID
     */
    private Integer id;

    /**
     * 学校ID
     */
    private Integer schoolId;

    //学校名字
    private String schoolName;


    /**
     * 头像地址
     */
    private String avatarURL;

    /**
     * 表白墙名字
     */
    private String wallName;

    /**
     * 表白墙描述
     */
    private String description;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    /**
     * 状态，0表示正常，1表示被禁用
     */
    private Integer status;


}
