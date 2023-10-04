package com.confession.service;

import com.confession.pojo.GlobalCarouselImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xpl
 * @since 2023年10月04日
 */
public interface GlobalCarouselImageService extends IService<GlobalCarouselImage> {

    /**
     * 获取全局轮播图 状态是没有被禁用的
     */
    List<GlobalCarouselImage> getGlobalCarouselImages();

    /**
     * 获取全部轮播图
     */
    List<GlobalCarouselImage> getAllGlobalCarouselImages();

    /**
     * 删除全局轮播图
     */
    void deleteCarouselImage(Integer id);

    /**
     * 添加全局轮播图
     */
    void addCarouselImage(String addr);

    /**
     * 关闭全局轮播图
     */
    void closeCarouselImage();

    /**
     * 开启全局轮播图
     */
    void openCarouselImage();
}
