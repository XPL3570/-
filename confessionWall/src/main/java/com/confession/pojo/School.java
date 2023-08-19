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
@TableName("school")
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * å­¦æ ¡ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * å­¦æ ¡åç§°
     */
    @TableField("SchoolName")
    private String schoolName;

    /**
     * å¤´åƒåœ°å€
     */
    @TableField("AvatarURL")
    private String avatarURL;

    /**
     * æè¿°å†…å®¹
     */
    @TableField("Description")
    private String description;

    /**
     * åˆ›å»ºè€…ID
     */
    @TableField("CreatorId")
    private Integer creatorId;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;


}
