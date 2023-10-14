package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年09月14日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("msgconfiguration")
public class MsgConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("Message")
    private String message;

    /**
     * 0 表示关闭 1表示开启
     */
    @TableField("MainSwitch")
    private Boolean mainSwitch;


}
