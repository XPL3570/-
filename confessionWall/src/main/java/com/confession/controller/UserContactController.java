package com.confession.controller;

import com.confession.comm.Result;
import com.confession.request.AgreeSetContactRequest;
import com.confession.request.ObtainContactInfoRequest;
import com.confession.service.UserContactService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户获取其他用户联系方式控制成
 *
 * @author 作者 xpl
 * @since 2023年10月24日
 */
@RestController
@RequestMapping("api/userContact")
public class UserContactController {

    @Resource
    private UserContactService service;

    /**
     * 用户发起获取其他用户联系方式
     */
    @PostMapping("obtainContact")
    public Result obtainContact(@RequestBody @Validated ObtainContactInfoRequest request){
        service.obtainContact(request);
        return Result.ok();
    }

    /**
     * 查看有多少条获取自己联系方式的count
     */
    @GetMapping("getYourOwnContactInfo")
    public Result getYourOwnContactInfo(){
        int count = service.getYourOwnContactInfo();
        return Result.ok(count);
    }

    /**
     * 用户设置获取自己联系的请求方式同意，或者不同意
     */
    @PostMapping("setObtainWxAgree")
    public Result setObtainWxAgree(@RequestBody @Validated AgreeSetContactRequest request){
        service.setAgreeOrNot(request);
        return Result.ok();
    }

    /**
     * 查看自己被获取的联系方式，和自己获取到的联系方式的联系方式
     */



}
