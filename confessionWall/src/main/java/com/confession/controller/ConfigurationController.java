package com.confession.controller;

import com.confession.comm.Result;
import com.confession.service.ConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ConfigurationController {

    @Resource
    private ConfigurationService configurationService;

    /**  两个接口， 都要超级管理员哦
     * 设置开启或关闭，
     * 设置内容
     *
     */

//
//    @PostMapping()
//    public Result

}
