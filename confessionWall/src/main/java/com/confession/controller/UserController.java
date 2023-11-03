package com.confession.controller;


import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.User;
import com.confession.request.*;
import com.confession.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.confession.comm.ResultCodeEnum.FREQUENT_MOD_OF_USER_INFO;


/**
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 这里登录会查询学校id绑定一个墙id
     * @return token和userInfo和墙id
     */
    @PostMapping("login")
    public Result login(@RequestBody @Validated LoginRequest request) {
        return userService.login(request);
    }

    /**
     * 用户没有使用过小程序，传递用户参数注册，openId数据库有唯一键，这里不查询数据库是否存在该openId了
     * 注册逻辑，实际上也可能是选择学校的逻辑，
     */
    @PostMapping("register")
    public Result register(@RequestBody @Validated RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("setWeChat")
    public Result updateWeChat(@RequestBody @Validated UserWeChatModRequest request) {
        userService.updateWeChat(request);
        return Result.ok();
    }


    @PostMapping("/avatar")
    public Result updateAvatar(@RequestBody @Validated UpdateAvatarRequest request) {
        userService.updateUserAttribute("avatar", request.getAvatarUrl());
        return Result.ok();
    }

    @PostMapping("/name")
    public Result updateName(@RequestBody @Validated UpdateNameRequest request) {
        userService.updateUserAttribute("name", request.getUsername());
        return Result.ok();
    }

    @GetMapping("/canModifyAvatar")
    public Result canModifyAvatar() {
        Integer userId = JwtInterceptor.getUser().getId();
        User user = userService.getById(userId);
        // 检查时间间隔
        if (!userService.checkTimeInterval(user.getUpdateTime())) {
            return Result.build(null, FREQUENT_MOD_OF_USER_INFO);
        }
        return Result.ok();
    }


    @GetMapping("admin/userList")
    public Result userList(@Validated @ModelAttribute PageTool pageTool,
                           @RequestParam(required = false) String schoolName,
                           @RequestParam(required = false) String userName,
                           @RequestParam(required = false) Integer status) {
        PageResult result = userService.selectUserList(pageTool, schoolName, userName, status);
        return Result.ok(result);
    }

    @PostMapping("admin/userStatusMod")
    public Result userStatusMod(@RequestBody @Validated UserStatusModRequest userStatusModRequest) {
        userService.statusMod(userStatusModRequest);
        return Result.ok();
    }

    @PostMapping("admin/usernameMod")
    public Result userNameMod(@RequestBody @Validated UserNameModRequest nameModRequest) {
        userService.userMod(nameModRequest);
        return Result.ok();
    }


}
