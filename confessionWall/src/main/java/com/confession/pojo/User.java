package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @author 作者 xpl
 * @since 2023年08月31日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @TableField("Username")
    private String username;

    /**
     * 学校ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     * 微信唯一ID
     */
    @TableField("OpenId")
    private String openId;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("UpdateTime")
    private LocalDateTime updateTime;

    /**
     * 是否可以获取微信
     */
    @TableField("CanObtainWeChat")
    private Boolean canObtainWeChat;

    /**
     * 微信账号
     */
    @TableField("WXAccount")
    private String wXAccount;

    /**
     * 性别，1表示男性，2表示女性，0表示未知
     */
    @TableField("Gender")
    private Integer gender;

    /**
     * 头像地址
     */
    @TableField("AvatarURL")
    private String avatarURL;

    /**
     * 用户状态，0表示正常，1表示被禁止发布，2表示禁止评论，3表示评论和发布都不可以
     */
    @TableField("Status")
    private Integer status;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean deleted;


}
