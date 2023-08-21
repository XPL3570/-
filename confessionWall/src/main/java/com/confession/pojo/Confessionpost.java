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
@TableName("confessionpost")
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
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    private Boolean deleted;


}
