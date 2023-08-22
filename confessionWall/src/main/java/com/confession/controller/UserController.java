package com.confession.controller;


import com.alibaba.fastjson.JSONObject;
import com.confession.comm.JwtConfig;
import com.confession.comm.Result;
import com.confession.config.WechatConfig;
import com.confession.pojo.School;
import com.confession.pojo.User;
import com.confession.request.RegisterRequest;
import com.confession.service.SchoolService;
import com.confession.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    public Result login(@RequestBody String code) {
        String openid = userService.codeByOpenid(code);
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


    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest request) throws JsonProcessingException {
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
            user.setUsername(request.getUserName());
            user.setAvatarURL(request.getAvatarUrl());
            userService.save(user);
            // 生成 token
            String token = JwtConfig.getJwtToken(user);
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("userInfo", user);
            // 返回 token 到小程序端
            return Result.ok(map);
        } else {
            // 学校不存在，告诉前端需要入驻学校
            return Result.build(206, "请先入驻学校");
        }


    }


}

