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
@TableName("admin")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学校ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     * 用户ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * 表白墙ID
     */
    @TableField("ConfessionWallId")
    private Integer confessionWallId;

    /**
     * 手机号
     */
    @TableField("PhoneNumber")
    private String phoneNumber;

    /**
     * 微信号
     */
    @TableField("WeChatId")
    private String weChatId;

    /**
     * 时间
     */
    @TableField("CreateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    /**
     * 权限标识，0表示普通管理员，1表示超级管理员
     */
    @TableField("Permission")
    private Integer permission;


    /**
     * 密码  一般管理员不需要
     */
    @TableField("Password")
    private String password;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean deleted;


}
