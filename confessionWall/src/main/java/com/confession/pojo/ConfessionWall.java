package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("confessionwall")
public class ConfessionWall implements Serializable {

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
     * 创建者用户ID
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
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * 是否禁用 状态，false表示正常，ture表示被禁用
     */
    @TableField("Status")
    private Boolean status;


    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean deleted;


}
