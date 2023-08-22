package com.confession.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
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
import me.chanjar.weixin.common.error.WxErrorException;
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
    private WechatConfig wechatConfig;

    @Resource
    private SchoolService schoolService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private WxMaService wxMaService;

    @PostMapping("login")
    public Result login(@RequestBody String code) {
        String appId = wechatConfig.getAppId();
        String secret = wechatConfig.getAppSecret();

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            // 解析响应体，提取 openId
            // 假设响应体为 JSON 格式：{"openid":"xxxxxxxxxxxxxxx","session_key":"yyyyyyyyyyyyyy"}
            // 这里使用了简单的字符串截取来提取 openId，请根据实际情况使用合适的 JSON 解析方式
            int startIndex = responseBody.indexOf("\"openid\":\"") + 10;
            int endIndex = responseBody.indexOf("\"", startIndex);
            String openid = responseBody.substring(startIndex, endIndex);
            // 根据 openid 查询数据库，看是否已存在该用户
            User user = userService.findByOpenid(openid);
            if (user == null) {
                return Result.build(206, "请选择学校");
            }
            String token = JwtConfig.getJwtToken(user);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("token", token);
            responseMap.put("username", user.getUsername());
            responseMap.put("avatarURL", user.getAvatarURL());

            return Result.ok(responseMap);
        } else {
            // 处理请求失败的情况
            return Result.fail("登录失败");
        }

    }
    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest request) throws WxErrorException {
        WxMaUserService userService = wxMaService.getUserService();
        System.out.println(wxMaService.getUserService().toString());
        System.out.println(request.toString());

        WxMaJscode2SessionResult sessionResult = userService.getSessionInfo(request.getCode());
        String openid = sessionResult.getOpenid();
        String sessionKey = sessionResult.getSessionKey();
        WxMaUserInfo userInfo = userService.getUserInfo(sessionKey, request.getEncryptedData(), request.getIv());
        String nickname = userInfo.getNickName();
        String avatarUrl = userInfo.getAvatarUrl();
        String gender = userInfo.getGender();

        System.out.println("昵称，id，头像地址，性别");
        System.out.println(nickname);
        System.out.println(openid);
        System.out.println(avatarUrl);
        System.out.println(gender);

        return null;
    }


//    @PostMapping("register")
//    public Result register(@RequestBody RegisterRequest request) throws JsonProcessingException {
//        System.out.println(request);
//
//        // 解析 JSON 数据获取 code
//        String receivedCode = request.getCode();
//
//        // 例如，调用微信开放平台的接口，通过 code 获取用户的 openid 和 session_key
//        String appId = wechatConfig.getAppId();
//        String secret = wechatConfig.getAppSecret();
//
//        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
//                "&secret=" + secret +
//                "&js_code=" + receivedCode +
//                "&grant_type=authorization_code";
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            String responseBody = response.getBody();
//
//            // 解析 JSON 数据获取 openid 和 session_key
//            Map<String, String> jsonResponse = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {
//            });
//            String openid = jsonResponse.get("openid");
//            String sessionKey = jsonResponse.get("session_key");
//            System.out.println("openid"+openid);
//            System.out.println("sessionKey"+sessionKey);
//
//            // 调用微信的用户信息接口，获取用户的昵称、头像地址和性别
//            String userInfoUrl2 = "https://api.weixin.qq.com/sns/userinfo?access_token=" + sessionKey +
//                    "&openid=" + openid;
//
//            ResponseEntity<String> userInfoResponse = restTemplate.getForEntity(userInfoUrl2, String.class);
//
//            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
//                String userInfoResponseBody = userInfoResponse.getBody();
//                System.out.println(userInfoResponseBody);
//
//                // 解析用户信息的 JSON 数据获取昵称和头像地址
//                Map<String, String> userInfoJson = objectMapper.readValue(userInfoResponseBody, new TypeReference<Map<String, String>>() {
//                });
//                String nickname = userInfoJson.get("nickname");
//                String avatarUrl = userInfoJson.get("headimgurl");
//                System.out.println(nickname);
//                System.out.println(avatarUrl);
//
//                // 查询学校是否存在
//                School school = schoolService.findBySchoolName(request.getSchoolName());
//                if (school != null) {
//                    User user = new User();
//                    user.setOpenId(openid);
//                    user.setUsername(nickname);
//                    user.setAvatarURL(avatarUrl);
//
//                    // 学校存在，绑定学校ID
//                    user.setSchoolId(school.getId());
//                    userService.save(user);
//                    // 生成 token
//                    String token = JwtConfig.getJwtToken(user);
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("token", token);
//                    map.put("userInfo", user);
//                    // 返回 token 到小程序端
//                    return Result.ok(map);
//                } else {
//                    // 学校不存在，告诉前端需要入驻学校
//                    return Result.build(206, "请先入驻学校");
//                }
//            } else {
//                // 处理请求失败的情况
//                return Result.fail("获取用户信息失败");
//            }
//        } else {
//            // 处理请求失败的情况
//            return Result.fail("获取用户id失败");
//        }
//
//    }


}

