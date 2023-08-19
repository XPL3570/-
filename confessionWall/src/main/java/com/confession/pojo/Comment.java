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
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * è¯„è®ºè®°å½•ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * å…³è”çš„è¡¨ç™½å¢™å‘å¸ƒå†…å®¹å®¡æ ¸è¡¨ID
     */
    @TableField("ConfessionPostReviewId")
    private Integer confessionPostReviewId;

    /**
     * ç”¨æˆ·ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * è¯„è®ºå†…å®¹
     */
    @TableField("CommentContent")
    private String commentContent;

    /**
     * è¯„è®ºæ—¶é—´
     */
    @TableField("CommentTime")
    private LocalDateTime commentTime;

    /**
     * é€»è¾‘åˆ é™¤æ ‡å¿—
     */
    @TableField("IsDeleted")
    private Boolean deleted;


}
