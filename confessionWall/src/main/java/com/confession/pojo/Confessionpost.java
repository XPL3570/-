package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月31日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("confessionpost")
@ToString
public class Confessionpost implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发布内容ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属表白墙ID
     */
    @TableField("WallId")
    private Integer wallId;

    /**
     * 发布者用户ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * 发布标题
     */
    @TableField("Title")
    private String title;

    /**
     * 发布内容文字
     */
    @TableField("TextContent")
    private String textContent;

    /**
     * 发布内容图片URL
     */
    @TableField("ImageURL")
    private String imageURL;

    /**
     * 投稿时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * 实际发布时间
     */
    @TableField("PublishTime")
    private LocalDateTime publishTime;

    /**
     * 发布状态，0表示待审核，1表示审核通过，2表示审核拒绝
     */
    @TableField("PostStatus")
    private Integer postStatus;

    /**
     * 是否匿名
     */
    @TableField("isAnonymous")
    private Boolean isAnonymous;

    /**
     * 是否为管理员发布的内容'
     */
    @TableField("IsAdminPost")
    private Boolean isAdminPost;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean deleted;

}
