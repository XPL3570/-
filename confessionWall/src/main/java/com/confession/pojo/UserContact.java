package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_contact
 */
@TableName(value ="user_contact")
@Data
public class UserContact implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 请求方用户ID
     */
    @TableField(value = "RequesterId")
    private Integer requesterId;

    /**
     * 接收方用户ID
     */
    @TableField(value = "ReceiverId")
    private Integer receiverId;

    /**
     * 申请理由，最长20个字符
     */
    @TableField(value = "ApplicationReason")
    private String applicationReason;

    /**
     * 联系方式值
     */
    @TableField(value = "ContactValue")
    private String contactValue;

    /**
     * 状态-0未同意，1同意获取，2拒绝获取
     */
    @TableField(value = "Status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UpdateTime")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}