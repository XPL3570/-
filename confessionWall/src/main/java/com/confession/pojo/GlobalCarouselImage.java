package com.confession.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xpl
 * @since 2023年10月04日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("globalcarouselimage")
public class GlobalCarouselImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Integer id;

    /**
     * 轮播图地址
     */
    @TableField("CarouselImage")
    private String carouselImage;



    /**
     * 是否禁用 false正常，true禁用
     */
    @TableField("IsDisable")
    private Boolean isDisable;

    /**
     * 是否删除
     */

    @TableField("IsDeleted")
    @TableLogic
    private Boolean isDeleted;


}
