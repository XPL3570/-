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
 * @since 2023年08月19日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("confessionpostreview")
public class Confessionpostreview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * å®¡æ ¸è®°å½•ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * å‘å¸ƒå†…å®¹IDåˆ—è¡¨
     */
    @TableField("PostIds")
    private String postIds;

    /**
     * å®¡æ ¸äººID
     */
    @TableField("ReviewerId")
    private Integer reviewerId;

    /**
     * å®¡æ ¸çŠ¶æ€
     */
    @TableField("ReviewStatus")
    private String reviewStatus;

    /**
     * å®¡æ ¸æ—¶é—´
     */
    @TableField("ReviewTime")
    private LocalDateTime reviewTime;


}
