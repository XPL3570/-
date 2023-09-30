package com.confession.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.config.JwtConfig;
import com.confession.dto.UserManageDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.Confessionwall;
import com.confession.pojo.School;
import com.confession.pojo.User;
import com.confession.request.*;
import com.confession.service.AdminService;
import com.confession.service.ConfessionwallService;
import com.confession.service.SchoolService;
import com.confession.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.confession.comm.ResultCodeEnum.SCHOOL_NOT_SETTLED;


/**
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private SchoolService schoolService;

    @Resource
    private AdminService adminService;


    @Resource
    private ConfessionwallService confessionwallService;

    /**
     * 这里登录会查询学校id绑定一个墙id
     *
     * @param request
     * @return token和userInfo和墙id
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginRequest request) {
        if (request.getCode() == null || request.getCode() == "") {
            throw new WallException("code不能是null", 201);
        }
        String openid = userService.codeByOpenid(request.getCode());
        if (openid == null) {
            throw new WallException("获取openid失败", 244);
        }
        // 根据 openid 查询数据库，看是否已存在该用户
        User user = userService.findByOpenid(openid);
        if (user == null || user.getSchoolId() == null) {
            return Result.build(206, "请选择学校");
        }
        //查询该学校下的一个墙id，如果有多个就返回第一个
        Confessionwall wall = confessionwallService.selectSchoolInWallOne(user.getSchoolId());
        String token = JwtConfig.getJwtToken(user);
        boolean isAdmin = adminService.isAdmin(user.getId(), wall.getId());
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", token);
        responseMap.put("userInfo", user);
        responseMap.put("wall", wall);
        responseMap.put("isAdmin", isAdmin);
        System.out.println("token=" + token);
        return Result.ok(responseMap);

    }

    /**
     * 用户没有使用过小程序，传递用户参数注册，openId数据库有唯一键，这里不查询数据库是否存在该openId了
     *
     * 注册逻辑，实际上也可能是选择学校的逻辑，
     *  todo 前端修改注册逻辑  就是选择学校的逻辑
     *
     * @param request
     * @return
     */
    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest request) {

        // 解析 JSON 数据获取 code
        String code = request.getCode();
        String openid = userService.codeByOpenid(code);

        School school;
        // 查询用户是否已经存在
        User user = userService.findByOpenid(openid);
        if (user == null) {
            // 如果用户不存在，创建一个新的用户
            user = new User();
            user.setOpenId(openid);
            user.setUsername(request.getUserName());
            user.setAvatarURL(request.getAvatarUrl());

            // 查询学校是否存在
            school = schoolService.findBySchoolName(request.getSchoolName());
            if (school != null) {
                user.setSchoolId(school.getId());
            } else {
                user.setSchoolId(null);
            }

            // 插入新的用户
            userService.save(user);
        } else {
            // 更新用户的信息
            user.setUsername(request.getUserName());
            user.setAvatarURL(request.getAvatarUrl());

            // 查询学校是否存在
            school = schoolService.findBySchoolName(request.getSchoolName());
            if (school != null) {
                user.setSchoolId(school.getId());
            } else {
                user.setSchoolId(null);
            }

            // 更新用户的信息
            userService.updateById(user);
        }

        // 如果学校不存在，返回token

        if (school == null) {
            return Result.build(JwtConfig.getJwtToken(user), SCHOOL_NOT_SETTLED);  //告诉用户学校没有注入，看是否要入注册学校
        }
        String token = JwtConfig.getJwtToken(user);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("userInfo", user);  //这里后面可以做一个过滤，把学校名字放进去
        // 返回 token 到小程序端
        return Result.ok(map);
    }


    @PostMapping("/avatar")
    public Result updateAvatar(HttpServletRequest req,
                               @RequestBody @Validated UpdateAvatarRequest request) {
        return updateUserAttribute(req, "avatar", request.getAvatarUrl());
    }

    @PostMapping("/name")
    public Result updateName(HttpServletRequest req,
                             @RequestBody @Validated UpdateNameRequest request) {
        return updateUserAttribute(req, "name", request.getUsername());
    }

    @GetMapping("/canModifyAvatar")
    public Result canModifyAvatar() {
//        int userId = Integer.valueOf(JwtConfig.getIdByJwtToken(req)); // 从请求头的token中获取用户ID
        Integer userId = JwtInterceptor.getUser().getId();
        User user = userService.getById(userId);
        // 检查时间间隔
        if (!checkTimeInterval(user.getUpdateTime())) {
            return Result.build(400, "修改头像或名字的时间间隔不足三天");
        }
        return Result.ok();
    }

    @GetMapping("admin/userList")
    public Result userList(@ModelAttribute PageTool pageTool,
                           @RequestParam(required = false) String schoolName,
                           @RequestParam(required = false) String userName,
                           @RequestParam(required = false) Integer status) {

        PageResult result=userService.selectUserList(pageTool,schoolName,userName,status);
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

    /**
     * 判断是否符合修改需求，修改用户头像或名字
     *
     * @param req
     * @param attributeName
     * @param attributeValue
     * @return
     */
    private Result updateUserAttribute(HttpServletRequest req, String attributeName, String attributeValue) {
        int userId = Integer.valueOf(JwtConfig.getIdByJwtToken(req)); // 从请求头的token中获取用户ID
        User user = userService.getById(userId);
        if (user == null) {
            return Result.build(404, "用户不存在");
        }

        // 检查时间间隔
        if (!checkTimeInterval(user.getUpdateTime())) {
            return Result.build(400, "修改头像或名字的时间间隔不足三天");
        }

        // 更新属性和更新时间
        if ("avatar".equals(attributeName)) {
            user.setAvatarURL(attributeValue);
        } else if ("name".equals(attributeName)) {
            user.setUsername(attributeValue);
        }
        user.setUpdateTime(LocalDateTime.now()); // 使用java.time.LocalDateTime类获取当前时间
        userService.updateById(user);
        return Result.ok();
    }


    private boolean checkTimeInterval(LocalDateTime lastUpdateTime) {
        // 计算当前时间与上次更新时间的间隔
        LocalDateTime currentTime = LocalDateTime.now(); // 使用java.time.LocalDateTime类获取当前时间
        Duration interval = Duration.between(lastUpdateTime, currentTime);
        Duration threeDays = Duration.ofDays(3);
        boolean res = interval.compareTo(threeDays) >= 0;
        System.out.println("res=" + res);
        return res;
    }


}
