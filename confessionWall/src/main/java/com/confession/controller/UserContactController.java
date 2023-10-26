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
    @GetMapping("getYourOwnContactCount")
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
     * 查看要自己的联系方式请求 这里不分页 最多20条 加入缓存，同意的时候删除缓存
     * todo 缓存没有做，还有前端这个页面可以做懒加载，还有看到自己未处理的添加申请的count  这个接口还没用到
     */
    @GetMapping("getYourOwnContact")
    public Result getYourOwnContact(){
        return Result.ok(service.getYourOwnContact());
    }

    /**
     * 查看你发送的申请 这里不分页 最多20条
     */
    @GetMapping("youApplicationSent")
    public Result applicationSent(){
        return Result.ok(service.youApplicationSent());
    }






}
