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
 * @since 2023年08月28日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("confessionwall")
public class Confessionwall implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表白墙ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学校ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     *  者用户ID
     */
    @TableField("CreatorUserId")
    private Integer creatorUserId;

    /**
     * 头像地址
     */
    @TableField("AvatarURL")
    private String avatarURL;

    /**
     * 表白墙名字
     */
    @TableField("WallName")
    private String wallName;

    /**
     * 表白墙描述
     */
    @TableField("Description")
    private String description;

    /**
     *  时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * 状态，0表示正常，1表示被禁用
     */
    @TableField("Status")
    private Integer status;


}
