package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName report_record 用户举报记录
 */
@TableName(value ="report_record")
@Data
public class ReportRecord implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "Id" ,type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField(value = "UserId")
    private Integer userId;

    /**
     * 举报投稿id
     */
    @TableField(value = "ReportId")
    private Integer reportId;

    /**
     * 举报时间
     */
    @TableField(value = "CreateTime")
    private LocalDateTime createTime;

    /**
     * 举报建议
     */
    @TableField(value = "Message")
    private String message;

    /**
     * 是否删除
     */
    @TableField("IsDeleted")
    @TableLogic
    private Boolean isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}