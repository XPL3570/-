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
@TableName("confessionpost")
public class Confessionpost implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * å‘å¸ƒå†…å®¹ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * æ‰€å±žè¡¨ç™½å¢™ID
     */
    @TableField("WallId")
    private Integer wallId;

    /**
     * å‘å¸ƒè€…ç”¨æˆ·ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * å‘å¸ƒå†…å®¹æ–‡å­—
     */
    @TableField("TextContent")
    private String textContent;

    /**
     * å‘å¸ƒå†…å®¹å›¾ç‰‡URL
     */
    @TableField("ImageURL")
    private String imageURL;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * é€»è¾‘åˆ é™¤æ ‡å¿—
     */
    @TableField("IsDeleted")
    private Boolean deleted;


}
