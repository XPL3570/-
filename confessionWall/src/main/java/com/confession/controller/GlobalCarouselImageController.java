package com.confession.controller;


import com.confession.comm.Result;
import com.confession.request.CarouseImageRequest;
import com.confession.request.GlobalCarouselSetRequest;
import com.confession.request.UpdateAvatarRequest;
import com.confession.service.GlobalCarouselImageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 全局轮播图 控制器
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
     * 获取全局图片是否禁用
     */
    @GetMapping("getCarouselIsDisabled")
    public Result getCarouselIsDisabled(){
        //可以优化成直接查询有没有被禁用的图片
        return Result.ok(service.getGlobalCarouselImages().size()==service.getAllGlobalCarouselImages().size());
    }

    /**
     *  获取全部全局图片，包括被禁用的,因为在未开启的时候，所有状态就是被禁用的
     */
    @GetMapping("getAllCarouselImage")
    public Result getAllCarouselImage(){
        return Result.ok(service.getAllGlobalCarouselImages());
    }

    /**
     * 设置全局轮播图假设无论他们的状态，如果再次给到一个数组，会和我数据库里面的数据进行对比，
     * 假如存在，就不用管，如果没有，就添加上去，如果这个列表里面有些数据库里面没有的东西，而数据库里面有，对应的就把数据库里面的东西删除了
     */
    @PostMapping("setGlobalCarousel")
    public Result setGlobalCarousel(@RequestBody @Validated GlobalCarouselSetRequest request){
        service.setGlobalCarousel(request);
        return Result.ok();
    }

    /**
     *  关闭全局轮播图 暂时禁用
     */
//    @PostMapping("closeCarouselImage")
    public Result closeCarouselImage(){
        service.closeCarouselImage();
        return Result.ok();
    }

    /**
     *  开启全局轮播图 暂时禁用
     */
//    @PostMapping("openCarouselImage")
    public Result openCarouselImage(){
        service.openCarouselImage();
        return Result.ok();
    }

    /**
     *  删除全局轮播图，暂时禁用
     */
//    @PostMapping("deleteCarouselImage")
    public Result deleteCarouselImage(@RequestBody CarouseImageRequest request){
        service.deleteCarouselImage(request.getId());
        return Result.ok();
    }

    /**
     *  添加全局轮播图，暂时禁用
     */
//    @PostMapping("addCarouselImage")
    public Result addCarouselImage(@RequestBody UpdateAvatarRequest request){
        service.addCarouselImage(request.getAvatarUrl());
        return Result.ok();
    }





}

