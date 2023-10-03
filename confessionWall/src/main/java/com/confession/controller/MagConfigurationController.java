package com.confession.controller;

import com.confession.comm.Result;
import com.confession.request.MsgGlobalPromptRequest;
import com.confession.service.MsgConfigurationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 这里是小程序的头部的提示的控制器
 */
@RestController
@RequestMapping("msg/admin")
public class MagConfigurationController {

    @Resource
    private MsgConfigurationService msgConfigurationService;

    /**  两个接口， 都要超级管理员哦
     * 设置开启或关闭，
     * 设置内容
     *
     */

    /**
     *  获取全局提示语
     * @return
     */
    @PostMapping("getGlobalPrompt")
    public Result getGlobalPrompt(){
        return Result.ok(msgConfigurationService.getGlobalPrompt());
    }

    //设置开启或关闭
    @PostMapping("setGlobalPrompts")
    public Result setGlobalPrompts(@RequestBody @Validated MsgGlobalPromptRequest request){
        msgConfigurationService.setGlobalPrompts(request);
        return Result.ok();
    }

}
