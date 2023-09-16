package com.confession.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.ResultCodeEnum;
import com.confession.config.WechatConfig;
import com.confession.dto.UserDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.UserMapper;
import com.confession.pojo.User;
import com.confession.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.confession.comm.RedisConstant.USER_DTO_PREFIX;


/**
 * <p>
 * 服务实现类
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

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public User findByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenId, openid);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public String codeByOpenid(String code) {
        // 例如，调用微信开放平台的接口，通过 code 获取用户的 openid 和 session_key
        String appId = wechatConfig.getAppId();
        String secret = wechatConfig.getAppSecret();
//        System.out.println("code=" + code);


        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                "&secret=" + secret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
//        System.out.println(url);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            // 使用 Fastjson 解析返回的 JSON 数据
            JSONObject json = JSONObject.parseObject(responseBody.toString());
            return json.getString("openid");
        } else {
            throw new WallException(ResultCodeEnum.GET_OPENID_FAIL);
        }
    }

    @Override
    public List<UserDTO> getUsersFromRedisOrDatabase(List<Integer> userIds) {
        List<UserDTO> users = new ArrayList<>();

        for (Integer userId : userIds) {
            UserDTO user = getUserFromRedisOrDatabase(userId);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

    @Override
    public UserDTO getUserFromRedisOrDatabase(Integer userId) {
        Object redisValue = redisTemplate.opsForValue().get(USER_DTO_PREFIX + userId);

        if (redisValue != null) {
            if (redisValue instanceof JSONObject) {
                JSONObject json = (JSONObject) redisValue;
                UserDTO userDTO = json.toJavaObject(UserDTO.class);
                return userDTO;
            } else {
                // 处理其他类型的值，或者抛出异常，取决于实际需求
            }
        }
        User dbUser = getById(userId);
        if (dbUser != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(dbUser.getUsername());
            userDTO.setAvatarURL(dbUser.getAvatarURL());
            redisTemplate.opsForValue().set(USER_DTO_PREFIX + userId, userDTO, 15, TimeUnit.MINUTES);
            return userDTO;
        } else {
            return null; // 如果数据库中也找不到用户信息，返回null或者抛出异常，根据实际情况进行处理。
        }
    }
}
