package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 微信账号
     */
    @TableField("WXAccount")
    private String wXAccount;

    /**
     * 性别，0表示女性，1表示男性，NULL表示未知
     */
    @TableField("Gender")
    private Boolean gender;

    /**
     * 头像地址
     */
    @TableField("AvatarURL")
    private String avatarURL;


}
