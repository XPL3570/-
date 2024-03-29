package com.confession.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@TableName("lotteryrecord")
public class LotteryRecord {

    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;
    @TableField("LotteryId")
    private Integer lotteryId;
    @TableField("UserId")
    private Integer userId;
    @TableField("DrawAt")
    private LocalDateTime drawAt;
}
