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

    /**
     * 轮播图地址
     */
    @TableField("CarouselImages")
    private String carouselImages;

    /**
     * 提示语
     */
    @TableField("Prompt")
    private String prompt;

    /**
     * 审核状态，0表示未审核，1表示通过，2未通过
     */
    @TableField("IsVerified")
    private Integer isVerified;


    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean deleted;


}
