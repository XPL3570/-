package com.confession.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 这里用户信息或者是请求方用户的，或者是接受方id，这里看你实际调用情况
 */
@Data
public class UserContactDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 请求方用户ID
     */
    private Integer requesterId;

    /**
     * 接收方用户ID
     */
    private Integer receiverId;

    private String username;
    private String avatarURL;

    /**
     * 申请理由，最长20个字符
     */
    private String applicationReason;

    /**
     * 联系方式值
     */
    private String contactValue;

    /**
     * 状态-0未同意，1同意获取，2拒绝获取
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
