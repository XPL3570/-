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
@TableName("confessionwall")
public class Confessionwall implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * è¡¨ç™½å¢™ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * å­¦æ ¡ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     * åˆ›å»ºè€…ç”¨æˆ·ID
     */
    @TableField("CreatorUserId")
    private Integer creatorUserId;

    /**
     * å¤´åƒåœ°å€
     */
    @TableField("AvatarURL")
    private String avatarURL;

    /**
     * è¡¨ç™½å¢™åå­—
     */
    @TableField("WallName")
    private String wallName;

    /**
     * è¡¨ç™½å¢™æè¿°
     */
    @TableField("Description")
    private String description;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * çŠ¶æ€ï¼Œ0è¡¨ç¤ºæ­£å¸¸ï¼Œ1è¡¨ç¤ºè¢«ç¦ç”¨
     */
    @TableField("Status")
    private Integer status;


}
