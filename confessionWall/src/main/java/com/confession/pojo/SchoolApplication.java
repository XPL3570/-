package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName school_application
 */
@TableName(value ="school_application")
@Data
public class SchoolApplication implements Serializable {
    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 学校ID
     */
    @TableField("SchoolId")
    private Integer schoolId;


    /**
     * 微信号
     */
    @TableField("WechatNumber")
    private String wechatNumber;

    /**
     * 手机号
     */
    @TableField("PhoneNumber")
    private String phoneNumber;

    /**
     * 逻辑删除标志
     */
    @TableField("IsDeleted")
    @TableLogic
    private Integer isdeleted;

    /**
     * 审核状态，0表示未审核，1表示通过，2表示未通过
     */
    @TableField("IsApproved")
    private Integer isApproved;

    /**
     * 审核通过的管理员ID
     */
    @TableField("ApprovedBy")
    private Integer approvedby;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}