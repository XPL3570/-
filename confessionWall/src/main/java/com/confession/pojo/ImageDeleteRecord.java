package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName image_delete_record
 */
@TableName(value ="image_delete_record")
@Data
public class ImageDeleteRecord implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 图片路径
     */
    @TableField("ImagePath")
    private String imagePath;

    /**
     * 用户ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date createTime;

    /**
     * 是否已删除，默认为false
     */
    @TableField("IsDeleted")
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}