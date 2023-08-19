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
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ç”¨æˆ·ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * ç”¨æˆ·å
     */
    @TableField("Username")
    private String username;

    /**
     * å­¦æ ¡ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     * å¾®ä¿¡ID
     */
    @TableField("WechatId")
    private String wechatId;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;

    /**
     * å¾®ä¿¡è´¦å·
     */
    @TableField("WXAccount")
    private String wXAccount;

    /**
     * æ€§åˆ«ï¼Œ0è¡¨ç¤ºå¥³æ€§ï¼Œ1è¡¨ç¤ºç”·æ€§ï¼ŒNULLè¡¨ç¤ºæœªçŸ¥
     */
    @TableField("Gender")
    private Boolean gender;

    /**
     * å¤´åƒåœ°å€
     */
    @TableField("AvatarURL")
    private String avatarURL;


}
