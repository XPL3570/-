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
 * @since 2023年09月16日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("lottery")
public class Lottery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 抽奖记录的唯一标识
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
     * 性别
     */
    @TableField("Gender")
    private Integer gender;

    /**
     * 图片地址
     */
    @TableField("ImageUrl")
    private String imageUrl;

    /**
     * 联系方式
     */
    @TableField("ContactInfo")
    private String contactInfo;

    /**
     * 介绍
     */
    @TableField("Introduction")
    private String introduction;

    /**
     * 记录创建时间
     */
    @TableField("CreatedAt")
    private LocalDateTime createdAt;

    /**
     * 被抽到次数
     */
    @TableField("DrawCount")
    private Integer drawCount;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    private Boolean deleted;


}
