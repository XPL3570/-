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
@TableName("confessionpostreview")
public class Confessionpostreview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审核记录ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发布内容ID列表
     */
    @TableField("PostIds")
    private String postIds;

    /**
     * 审核人ID
     */
    @TableField("ReviewerId")
    private Integer reviewerId;

    /**
     * 审核状态
     */
    @TableField("ReviewStatus")
    private String reviewStatus;

    /**
     * 审核时间
     */
    @TableField("ReviewTime")
    private LocalDateTime reviewTime;


}
