package com.confession.controller;


import com.confession.comm.Result;
import com.confession.request.CarouseImageRequest;
import com.confession.request.UpdateAvatarRequest;
import com.confession.service.GlobalCarouselImageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 全局轮播图
 *
 * 这里有一个全局轮播图的开关，可以把所有的轮播图信息设置成禁用，前端也先通过这个判断禁用图片和总图片的数量来判断这个开关是否开启
 *
 * 这里没有单独添加一个表，或者其他的手段达到控制全局轮播图的开启和关闭也是
 * 因为数量有限，所以直接通过状态来控制，设置关闭的之后，只是把所有的状态改成禁用
 * 所以也没有单独禁用某一个图片
 *
 * @author xpl
 * @since 2023年10月04日
 */
@RestController
@RequestMapping("/carouselImage/admin")
public class GlobalCarouselImageController {

    @Resource
    private GlobalCarouselImageService service;

    /**
     * 获取全局图片,必须是没被禁用的
     */
    @GetMapping("getCarouselImage")
    public Result getCarouselImage(){
        return Result.ok(service.getGlobalCarouselImages());
    }

    /**
     *  获取全部全局图片，包括被禁用的
     */
    @GetMapping("getAllCarouselImage")
    public Result getAllCarouselImage(){
        return Result.ok(service.getAllGlobalCarouselImages());
    }

    /**
     *  删除全局轮播图
     */
    @PostMapping("deleteCarouselImage")
    public Result deleteCarouselImage(@RequestBody CarouseImageRequest request){
        service.deleteCarouselImage(request.getId());
        return Result.ok();
    }

    /**
     *  添加全局轮播图
     */
    @PostMapping("addCarouselImage")
    public Result addCarouselImage(@RequestBody UpdateAvatarRequest request){
        service.addCarouselImage(request.getAvatarUrl());
        return Result.ok();
    }

    /**
     *  关闭全局轮播图
     */
    @PostMapping("closeCarouselImage")
    public Result closeCarouselImage(){
        service.closeCarouselImage();
        return Result.ok();
    }

    /**
     *  开启全局轮播图
     */
    @PostMapping("openCarouselImage")
    public Result openCarouselImage(){
        service.openCarouselImage();
        return Result.ok();
    }



}

