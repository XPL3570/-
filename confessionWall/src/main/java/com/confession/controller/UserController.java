package com.confession.controller;


import com.confession.comm.JwtConfig;
import com.confession.comm.Result;
import com.confession.globalConfig.exception.WallException;
import com.confession.pojo.School;
import com.confession.pojo.User;
import com.confession.request.LoginRequest;
import com.confession.request.RegisterRequest;
import com.confession.service.SchoolService;
import com.confession.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


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

    @PostMapping("login")
    public Result login(@RequestBody LoginRequest request) {
        String openid = userService.codeByOpenid(request.getCode());
        if (openid == null) {
            throw new WallException("获取openid失败", 244);
        }
        // 根据 openid 查询数据库，看是否已存在该用户
        User user = userService.findByOpenid(openid);
        if (user == null) {
            return Result.build(206, "请选择学校");
        }
        String token = JwtConfig.getJwtToken(user);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", token);
        responseMap.put("userInfo", user);

        return Result.ok(responseMap);

    }

    /**
     * 用户没有使用过小程序，传递用户参数注册，openId数据库有唯一键，这里不查询数据库是否存在该openId了
     *
     * @param request
     * @return
     */
    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest request) {
        System.out.println(request);

        // 查询学校是否存在
        School school = schoolService.findBySchoolName(request.getSchoolName());
        if (school != null) {
            User user = new User();
            // 解析 JSON 数据获取 code
            String code = request.getCode();
            String openid = userService.codeByOpenid(code);
            user.setOpenId(openid);
            user.setSchoolId(school.getId());
            user.setUserName(request.getUserName());
            user.setAvatarURL(request.getAvatarUrl());
            try {
                userService.save(user);
            } catch (Exception e) {
                System.out.println("用户openId是" + user.getOpenId() + "已经存在，注册失败");
                return Result.build(201, "该用户已经存在，注册失败");
            }
            // 生成 token
            String token = JwtConfig.getJwtToken(user);
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("userInfo", user);  //这里后面可以做一个过滤，把学校名字放进去
            // 返回 token 到小程序端
            return Result.ok(map);
        } else {
            // 学校不存在，告诉前端需要入驻学校
            return Result.build(206, "请先入驻学校");
        }


    }


}

