package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者
 * @since 2023年08月31日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论记录ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的表白墙发布内容表ID
     */
    @TableField("ConfessionPostReviewId")
    private Integer confessionPostReviewId;

    /**
     * 父级评论ID
     */
    @TableField("ParentCommentId")
    private Integer parentCommentId;

    /**
     * 用户ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * 评论内容
     */
    @TableField("CommentContent")
    private String commentContent;

    /**
     * 评论时间
     */
    @TableField("CommentTime")
    private LocalDateTime commentTime;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    private Boolean deleted;


}
