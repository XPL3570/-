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
@TableName("admin")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ç®¡ç†å‘˜ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * å­¦æ ¡ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     * ç”¨æˆ·ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * æ‰‹æœºå·
     */
    @TableField("PhoneNumber")
    private String phoneNumber;

    /**
     * å¾®ä¿¡å·
     */
    @TableField("WeChatId")
    private String weChatId;

    /**
     * åˆ›å»ºæ—¶é—´
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;


}
