package com.confession.controller;


import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.request.SubReportRecordRequest;
import com.confession.service.ReportRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户举报记录控制器
 */
@RestController
@RequestMapping("api/report")
public class ReportRecordController {

    @Resource
    private ReportRecordService service;

    @PostMapping("sendReport") //接受举报投稿信息
    public Result sendReport(@RequestBody @Validated SubReportRecordRequest request){
        service.userSubmitsReport(JwtInterceptor.getUser().getId(),request);
        return Result.ok();
    }

    @GetMapping("admin/getReportList") //这里里面放了投稿者的id，后面可以直接去修改投稿者的状态，去调用别的Controller方法修改用户状态
    public Result getReportInfo(@ModelAttribute PageTool pageTool){
        return Result.ok(service.getReportInfoList(pageTool));
    }




}
