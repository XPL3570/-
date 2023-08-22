package com.confession.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.comm.ResultCodeEnum;
import com.confession.config.WechatConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.pojo.User;
import com.confession.mapper.UserMapper;
import com.confession.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;



/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private WechatConfig wechatConfig;


    @Override
    public User findByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenId,openid);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public String codeByOpenid(String code) {
        // 例如，调用微信开放平台的接口，通过 code 获取用户的 openid 和 session_key
        String appId = wechatConfig.getAppId();
        String secret = wechatConfig.getAppSecret();

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            // 使用 Fastjson 解析返回的 JSON 数据
            JSONObject json = JSONObject.parseObject(responseBody.toString());
            return json.getString("openid");
        }else {
            throw new WallException(ResultCodeEnum.GET_OPENID_FAIL);
        }
    }
}
