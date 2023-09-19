package com.confession.controller;


import com.confession.comm.Result;
import com.confession.request.RegisterSchoolRequest;
import com.confession.service.SchoolService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("api/school")
public class SchoolController {

    @Resource
    private SchoolService schoolService;



    /**
     * 拿到学校的提示语，可以是管理员可以设置是否读取管理员设置的统一提示语
     * @return
     */
    @GetMapping("getPromptMessage")
    public Result getPromptMessage(@ModelAttribute Integer schoolId){
        String prompt=schoolService.getPromptMessage(schoolId);
        return Result.ok(prompt);
    }

    /**
     * 注册学校的接口
     */
    @PostMapping("register")
    public Result registerSchool(@RequestBody @Validated RegisterSchoolRequest registerSchool){
        schoolService.register(registerSchool);
        return Result.ok();
    }

    /**
     * 超级管理员查看所有学校的接口  todo 如何做是不是超级管理员的校验？
     */



    /**
     * 超级管理员审核未通过审核的接口
     */





}

