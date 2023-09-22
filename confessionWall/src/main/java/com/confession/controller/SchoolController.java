package com.confession.controller;


import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.dto.SchoolApplicationDTO;
import com.confession.pojo.School;
import com.confession.request.RegisterSchoolRequest;
import com.confession.request.SchoolExamineRequest;
import com.confession.service.SchoolService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
     * 注册学校的接口  也包括注册者的信息  这里没有使用token
     */
    @PostMapping("register")
    public Result registerSchool(@RequestBody @Validated RegisterSchoolRequest registerSchool){
        Integer schoolId = schoolService.registerSchool(registerSchool);
        return Result.ok(schoolId);
    }

    /**
     * 超级管理员查看所有学校的接口  如何做是不是超级管理员的校验？拦截器里面
     */
    @GetMapping("admin/viewSchool")
    public Result viewSchool(@ModelAttribute PageTool pageTool){
        List<School> list=schoolService.viewSchool(pageTool);

        return Result.ok(list);
    }


    /**
     * 超级管理员查看未通过审核的接口
     */
    @GetMapping("admin/viewNoReview")
    public Result viewNoReview(@ModelAttribute PageTool pageTool){
        List<SchoolApplicationDTO> list=schoolService.viewNoReview(pageTool);
        return Result.ok(list);
    }

    /**
     * 审核学校
     */
    @PostMapping("admin/examine")
    public Result examinePost(@RequestBody SchoolExamineRequest schoolExamineRequest){
        schoolService.examinePost(schoolExamineRequest);
        return Result.ok();
    }





}

