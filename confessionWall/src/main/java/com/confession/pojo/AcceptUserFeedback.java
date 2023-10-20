package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName accept_user_feedback 用户反馈
 */
@TableName(value ="accept_user_feedback")
@Data
public class AcceptUserFeedback implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "Id")
    private Integer id;

    /**
     * 用户id
     */
    @TableField(value = "UserId")
    private Integer userid;

    /**
     * 消息
     */
    @TableField(value = "Message")
    private String message;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    private Date createTime;

    /**
     * 是否已读
     */
    @TableField(value = "IsRead")
    private Boolean isRead;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}