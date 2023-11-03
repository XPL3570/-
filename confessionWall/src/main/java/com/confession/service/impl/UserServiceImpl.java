package com.confession.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.comm.ResultCodeEnum;
import com.confession.config.JwtConfig;
import com.confession.config.WechatConfig;
import com.confession.dto.UserDTO;
import com.confession.dto.UserManageDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.UserMapper;
import com.confession.pojo.ConfessionWall;
import com.confession.pojo.School;
import com.confession.pojo.User;
import com.confession.request.*;
import com.confession.service.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.confession.comm.RedisConstant.NUMBER_USER_DELETIONS;
import static com.confession.comm.RedisConstant.USER_DTO_PREFIX;
import static com.confession.comm.ResultCodeEnum.*;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者 xpl
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

    @Resource
    private ImageService imageService;

    @Resource
    private SchoolService schoolService;

    @Resource
    private AdminService adminService;


    @Resource
    private ConfessionWallService confessionwallService;


    @Override
    public User findByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenId, openid);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Result login(LoginRequest request) {
        if (request.getCode() == null || request.getCode() == "") {
            throw new WallException("code不能是null", 201);
        }
        String openid = this.codeByOpenid(request.getCode());
        if (openid == null) {
            throw new WallException("获取openid失败", 244);
        }
        // 根据 openid 查询数据库，看是否已存在该用户
        User user = this.findByOpenid(openid);
        if (user == null || user.getSchoolId() == null) {
            return Result.build(206, "请选择学校");
        }
        user.setOpenId("");
        //查询该学校下的一个墙id，如果有多个就返回第一个
        ConfessionWall wall = confessionwallService.selectSchoolInWallOne(user.getSchoolId());
        String token = JwtConfig.getJwtToken(user);
        boolean isAdmin = adminService.isAdmin(user.getId(), wall.getId());
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", token);
        responseMap.put("userInfo", user);
        responseMap.put("wall", wall);
        responseMap.put("isAdmin", isAdmin);
//        System.out.println("token=" + token);
        return Result.ok(responseMap);
    }

    @Override
    public Result register(RegisterRequest request) {
        if (request.getUserName().contains("表白墙") || request.getUserName().contains("墙")) {
            throw new WallException("该用户名不能设置哦，您可以换一个哦", 400);
        }
        String code = request.getCode();
        String openid = this.codeByOpenid(code);

        School school;
        // 查询用户是否已经存在
        User user = this.findByOpenid(openid);
        if (user == null) {
            // 如果用户不存在，创建一个新的用户
            user = new User();
            user.setOpenId(openid);
            user.setUsername(request.getUserName());
            user.setAvatarURL(request.getAvatarUrl());
            user.setUpdateTime(LocalDateTime.now().minusDays(1));

            // 查询学校是否存在
            school = schoolService.findBySchoolName(request.getSchoolName());
            if (school != null) {
                user.setSchoolId(school.getId());
            } else {
                user.setSchoolId(null);
            }
            // 插入新的用户
            boolean save = this.save(user);
            if (!save){
                throw new WallException("写入用户失败",201);
            }
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
            this.updateById(user);
        }
        // 如果学校不存在，返回token
        if (school == null) {
            return Result.build(JwtConfig.getJwtToken(user), SCHOOL_NOT_SETTLED);  //告诉用户学校没有注入，看是否要入注册学校
        }
        String token = JwtConfig.getJwtToken(user);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("userInfo", user);  //这里后面可以做一个过滤，把学校名字放进去
        ConfessionWall wall = confessionwallService.selectSchoolInWallOne(user.getSchoolId());
        map.put("wall", wall);
        map.put("isAdmin", false);
        // 返回 token 到小程序端
        return Result.ok(map);
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

    @Override
    public void statusMod(UserStatusModRequest userStatusModRequest) {
        if (userStatusModRequest.getUserId() == null) {//多加一道校验，
            throw new WallException("修改用户状态失败", 201);
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userStatusModRequest.getUserId())
                .set(User::getStatus, userStatusModRequest.getStatus());
        userMapper.update(null, updateWrapper);
    }

    @Override
    public void userMod(UserNameModRequest nameModRequest) {
        if (nameModRequest.getUserId() == null) {//多加一道校验，
            throw new WallException("修改用户状态失败", 201);
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, nameModRequest.getUserId())
                .set(User::getUsername, nameModRequest.getUsername());
        userMapper.update(null, updateWrapper);
    }

    @Override
    public void updateUserAttribute(String attributeName, String attributeValue) {
        Integer userId = JwtInterceptor.getUser().getId();
        User user = this.getById(userId);
        if (user == null) {
            throw new WallException(USER_NOT_EXIST);
        }

        // 检查时间间隔
        if (!checkTimeInterval(user.getUpdateTime())) {
            throw new WallException( FREQUENT_MOD_OF_USER_INFO);
        }

        // 更新属性和更新时间
        if ("avatar".equals(attributeName)) {
            //这里切换头像就把之前的头像删除了
            DeleteImageRequest zj = new DeleteImageRequest();
            zj.setDeleteUrl(attributeValue);
            imageService.deleteImage(zj,userId);
            user.setAvatarURL(attributeValue);
        } else if ("name".equals(attributeName)) {
            if (attributeValue.contains("表白墙") || attributeValue.contains("墙")) {
                throw new WallException("该名字不能设置哦,您可以换一个哦", 400);
            }
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, attributeName);
            int count = this.count(queryWrapper);
            if (count > 1) {
                throw new WallException("该名字已存在", 400);
            }
            user.setUsername(attributeValue);
        }
        user.setUpdateTime(LocalDateTime.now()); // 使用java.time.LocalDateTime类获取当前时间
        this.updateById(user);

        //这里判断redis里面是否有key，有就更新一下缓存
        boolean exists = redisTemplate.hasKey(USER_DTO_PREFIX + userId);

        if (exists) {
            UserDTO userDTO = new UserDTO();
            User byId = this.getById(userId);
            userDTO.setAvatarURL(byId.getAvatarURL());
            userDTO.setUsername(byId.getUsername());
            // 更新缓存逻辑
            redisTemplate.opsForValue().set(USER_DTO_PREFIX + userId, userDTO, 15, TimeUnit.MINUTES);

        }
    }

    @Override
    public boolean checkTimeInterval(LocalDateTime lastUpdateTime) {
        // 计算当前时间与上次更新时间的间隔
        LocalDateTime currentTime = LocalDateTime.now(); // 使用java.time.LocalDateTime类获取当前时间
        Duration interval = Duration.between(lastUpdateTime, currentTime);
        Duration threeDays = Duration.ofDays(1);
        boolean res = interval.compareTo(threeDays) >= 0;
        System.out.println("res=" + res);
        return res;
    }

    @Override
    public void updateWeChat(UserWeChatModRequest request) {
        Integer userId = JwtInterceptor.getUser().getId();
        User user = userMapper.selectById(userId);
        if (user==null){
            throw new WallException(FAIL);
        }
        //检查修改间隔
        if (!checkTimeInterval(user.getUpdateTime())) {
            throw new WallException( FREQUENT_MOD_OF_USER_INFO);
        }
        user.setUpdateTime(LocalDateTime.now());
        user.setCanObtainWeChat(request.getCanObtainWeChat()).setWXAccount(request.getWxAccount());
        this.updateById(user);
    }

    @Override
    public void userCanDelete(Integer userId) {
        String key =  NUMBER_USER_DELETIONS + userId;
        ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
        // 获取当前用户的删除次数
        Integer deleteCount = valueOperations.get(key);
        if (deleteCount == null) {
            deleteCount = 0;
        }

        // 判断删除次数是否超过限制
        if (deleteCount >= 2) {
            throw new WallException(TOO_MANY_USER_DELETIONS);
        }
        // 更新删除次数并设置过期时间
        valueOperations.set(key, deleteCount + 1, 12, TimeUnit.HOURS);
    }

    @Override
    public PageResult selectUserList(PageTool pageTool, String schoolName, String userName, Integer status) {
        Page<User> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(schoolName)) {
            // 如果提供了学校名称，那么根据学校名称模糊查询学校ID
            List<Integer> schoolIds = schoolService.selectIdsByNameLike(schoolName);
            if (!schoolIds.isEmpty()) {
                // 如果找到了匹配的学校ID，那么添加到查询条件中
                queryWrapper.in(User::getSchoolId, schoolIds);
            }
        }
        if (status != null) {
            if (status == 0) {
                // 如果提供了用户状态，那么添加到查询条件中
                queryWrapper.eq(User::getStatus, 0);
            } else {
                queryWrapper.ne(User::getStatus, 0);
            }

        }
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like(User::getUsername, userName);
        }
        List<User> list = this.page(page, queryWrapper).getRecords();

        List<UserManageDTO> zjS = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult(zjS, page.getTotal(), list.size());
    }


    public UserManageDTO convertToDTO(User user) {
        UserManageDTO userDTO = new UserManageDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        // 根据学校 ID 查询学校名称，并设置到 DTO 对象中
        String schoolName = schoolService.getSchoolNameById(user.getSchoolId());
        userDTO.setSchoolName(schoolName);
        userDTO.setOpenId(user.getOpenId());
        userDTO.setCreateTime(user.getCreateTime());
        userDTO.setWXAccount(user.getWXAccount());
        userDTO.setGender(user.getGender());
        userDTO.setAvatarURL(user.getAvatarURL());
        userDTO.setStatus(user.getStatus());
        return userDTO;
    }
}
