package com.confession.controller;


import com.confession.comm.JwtConfig;
import com.confession.comm.Result;
import com.confession.config.WechatConfig;
import com.confession.pojo.User;
import com.confession.request.RegisterRequest;
import com.confession.service.UserService;
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
 * <p>
 *  前端控制器
 * </p>
 *
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
    private RestTemplate restTemplate;

    @PostMapping("login")
    public Result login(@RequestBody String code){
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
            String openid=responseBody.substring(startIndex, endIndex);
            // 根据 openid 查询数据库，看是否已存在该用户
            User user = userService.findByOpenid(openid);
            if (user == null) {
               return Result.build(206,"请选择学校");
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
    public Result register(@RequestBody RegisterRequest registerRequest){

        try {
            // 使用 Jackson 的 ObjectMapper 进行 JSON 解析
            ObjectMapper objectMapper = new ObjectMapper();

            // 解析 JSON 数据获取 code
//            Map<String, String> requestBody = objectMapper.readValue(code, new TypeReference<Map<String, String>>() {});
//            String receivedCode = requestBody.get("code");

            // 进行相应的验证和处理
            // 例如，调用微信开放平台的接口，通过 code 获取用户的 openid 和 session_key
            String appId = wechatConfig.getAppId();
            String secret = wechatConfig.getAppSecret();

            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                    "&secret=" + secret +
//                    "&js_code=" + receivedCode +
                    "&grant_type=authorization_code";

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();

                // 解析 JSON 数据获取 openid 和 session_key
                Map<String, String> jsonResponse = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {});
                String openid = jsonResponse.get("openid");
                String sessionKey = jsonResponse.get("session_key");

                // 根据 openid 查询数据库，看是否已存在该用户
                User user = userService.findByOpenid(openid);
                if (user == null) {
                    // 若用户不存在，则创建新用户并保存到数据库
                    user = new User();
                    user.setOpenId(openid);
                    // 其他必要的操作
                    userService.save(user);
                }

                // 生成 token
//                String token = generateToken(user.getId());

                // 返回 token 到小程序端
//                return Result.ok(token);
            } else {
                // 处理请求失败的情况
                return Result.fail("登录失败");
            }
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
            return Result.fail("登录失败");
        }

        return Result.ok();

    }



}

