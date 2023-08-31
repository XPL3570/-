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
@TableName("school")
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学校名称
     */
    @TableField("SchoolName")
    private String schoolName;

    /**
     * 头像地址
     */
    @TableField("AvatarURL")
    private String avatarURL;

    /**
     * 描述内容
     */
    @TableField("Description")
    private String description;

    /**
     * 创建者ID
     */
    @TableField("CreatorId")
    private Integer creatorId;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;


}
