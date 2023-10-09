package com.confession.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConfessionPostAdminDTO {

    private Integer id; // ID

    private Integer wallId;

    private String wallName; // 墙名称

    private String userName; // 用户名

    private String userAvatar; // 用户头像

    private String title; // 标题

    private String textContent; // 文本内容

    private String imageURL; // 图片URL

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime; // 发布时间

    private Integer postStatus; // 投稿状态 0是未审核，1是已经发布，2是审核未通过

    private Boolean isAnonymous; // 是否匿名

    private Boolean isAdminPost; // 是否管理员发布的帖子
}
