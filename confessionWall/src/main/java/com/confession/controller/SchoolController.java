package com.confession.controller;


import com.confession.comm.Result;
import com.confession.service.SchoolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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





}

