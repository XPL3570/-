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
@TableName("admin")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学校ID
     */
    @TableField("SchoolId")
    private Integer schoolId;

    /**
     * 用户ID
     */
    @TableField("UserId")
    private Integer userId;

    /**
     * 手机号
     */
    @TableField("PhoneNumber")
    private String phoneNumber;

    /**
     * 微信号
     */
    @TableField("WeChatId")
    private String weChatId;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime createTime;


}
