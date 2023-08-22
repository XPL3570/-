package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
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
     * 微信ID
     */
    @TableField("OpenId")
    private String openId;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    @TableField("UpdateTime")
    private LocalDateTime updateTime;

    /**
     * 微信账号
     */
    @TableField("WXAccount")
    private String wXAccount;

    /**
     * 性别:1 表示男性，2 表示女性，0 表示未知
     */
    @TableField("Gender")
    private Integer gender;

    /**
     * 头像地址
     */
    @TableField("AvatarURL")
    private String avatarURL;


}
