package com.confession.controller;

import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.dto.AcceptUserFeedbackDTO;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.request.SubmitFeedbackRequest;
import com.confession.service.AcceptUserFeedbackService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户反馈控制器
 */
@RestController
@RequestMapping("/info/feedback")
public class AcceptUserFeedbackController {

    @Resource
    private AcceptUserFeedbackService service;

    @PostMapping("submit")
    public Result submit(@RequestBody @Validated SubmitFeedbackRequest request){
        Integer userId = JwtInterceptor.getUser().getId();
        service.userSubmit(userId,request);
        return Result.ok();
    }

    @GetMapping("/admin/noReadObCount")  //获取未读用户反馈数
    public Result obtainingFeedbackInfoCount(){
        int count= service.getNoReadCount();
        return Result.ok(count);
    }

    @GetMapping("/admin/noReadObtaining")  //获取未读反馈 一次8条
    public Result obtainingFeedbackNoReadInfo(){
        List<AcceptUserFeedbackDTO> res = service.getNoReadInfo();
        return Result.ok(res);
    }


    @GetMapping("/admin/allObtaining")  //获取所有用户反馈,反应查看、最后写，可不用
    public Result obtainingFeedbackInformation(@ModelAttribute PageTool pageTool){
        PageResult res=service.obtainingFeedbackInformation(pageTool);
        return Result.ok(res);
    }



}
