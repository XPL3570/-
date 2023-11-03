package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.config.WallConfig;
import com.confession.dto.UserContactDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.User;
import com.confession.pojo.UserContact;
import com.confession.request.AgreeSetContactRequest;
import com.confession.request.ObtainContactInfoRequest;
import com.confession.service.UserContactService;
import com.confession.mapper.UserContactMapper;
import com.confession.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.confession.comm.RedisConstant.*;
import static com.confession.comm.ResultCodeEnum.*;

/**
 * 获取微信号
 */
@Service
public class UserContactServiceImpl extends ServiceImpl<UserContactMapper, UserContact>
    implements UserContactService{

    @Resource
    private UserContactMapper mapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private WallConfig wallConfig;

    @Override
    public int getYourOwnContactInfo() {
        Integer userId = JwtInterceptor.getUser().getId();
        LambdaQueryWrapper<UserContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserContact::getReceiverId,userId);
        wrapper.eq(UserContact::getStatus,0);
        return mapper.selectCount(wrapper);
    }

    @Override
    public void obtainContact(ObtainContactInfoRequest request) {
        Integer userId = JwtInterceptor.getUser().getId();
        User receiverUser = userService.getById(request.getReceiverId());
        if (receiverUser==null){
            throw new WallException(USER_NOT_EXIST);
        }
        if (!receiverUser.getCanObtainWeChat()){
            throw new WallException(UNABLE_OBTAIN_USER_WECHAT);
        }
        //用户发起方月可添加数量
        if (this.countByUserIdWithinOneMonthUsing(userId)>=wallConfig.getCanAddFriendsMonth()){
            throw new WallException(FREQUENT_USER_ACCESS_WX);
        }
        //被获取方月被获取次数
        if (this.countByUserIdObtainedOneMonth(request.getReceiverId())>=wallConfig.getCanAcceptFriendsMonth()){
            throw new WallException(FREQUENT_USER_OBTAIN_WECHAT);
        }
        if (userService.getById(userId).getStatus()==3){ //如果被禁止投稿和评论了，就不可以加好友，然后提示加好友平凡
            throw new WallException(FREQUENT_USER_ACCESS_WX);
        }

        LambdaQueryWrapper<UserContact> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserContact::getRequesterId, userId).eq(UserContact::getReceiverId,request.getReceiverId());

        if (mapper.selectCount(wrapper)>0){
            throw new WallException(APPLICATION_HAS_ALREADY);
        }

        UserContact userContact = new UserContact();
        userContact.setRequesterId(userId);
        userContact.setReceiverId(request.getReceiverId());
        userContact.setApplicationReason(request.getApplicationReason());
        userContact.setStatus(0);
        this.save(userContact);
        //删除被申请方的好友申请缓存
        redisTemplate.delete(USER_FRIEND_APPLICATION+request.getReceiverId());
        //删除自己的添加好友缓存
        redisTemplate.delete(USER_ADD_FRIENDS+userId);
    }

    @Override
    public void setAgreeOrNot(AgreeSetContactRequest request) {
        Integer userId = JwtInterceptor.getUser().getId();
        UserContact contact = mapper.selectById(request.getUserContactId());
        if (contact==null||!contact.getReceiverId().equals(userId)){
            throw new WallException(FAIL); //这里是想修改的id不对，直接报错
        }
        if (request.getIsAgree()){
            contact.setStatus(1);
            contact.setContactValue(userService.getById(userId).getWXAccount());
        }else {
            contact.setStatus(2);
        }
        this.updateById(contact);
        //删除自己的好友申请缓存
        redisTemplate.delete(USER_FRIEND_APPLICATION+userId);
        //删除别人的添加好友缓存
        redisTemplate.delete(USER_ADD_FRIENDS+contact.getReceiverId());
    }


    @Override
    public List<UserContactDTO> youApplicationSent() {
        Integer userId = JwtInterceptor.getUser().getId();
        Object cachedValue = redisTemplate.opsForValue().get(USER_ADD_FRIENDS + userId);
        if (cachedValue != null) {
            if (cachedValue instanceof List) {
                return (List<UserContactDTO>) cachedValue;
            } else {
                // 缓存中存储的是空值，直接返回空列表
                return Collections.emptyList();
            }
        }
        List<UserContactDTO> list = mapper.findApplicationSentContactList(userId);
        if (list.isEmpty()) {
            // 数据库中不存在该数据，将空值存入缓存
            redisTemplate.opsForValue().set(USER_ADD_FRIENDS + userId, Collections.emptyList(),20, TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForValue().set(USER_ADD_FRIENDS + userId, list,20, TimeUnit.MINUTES);
        }
        return list;
    }


    @Override
    public List<UserContactDTO> getYourOwnContact() {
        Integer userId = JwtInterceptor.getUser().getId();
        Object value = redisTemplate.opsForValue().get(USER_FRIEND_APPLICATION + userId);
        if (value != null) {
            if (value instanceof List) {
                return (List<UserContactDTO>) value;
            } else {
                // handle the case when cache value is not a list
                redisTemplate.delete(USER_FRIEND_APPLICATION + userId);
            }
        }
        List<UserContactDTO> list = mapper.findGetMeContactList(userId);
        if (list.isEmpty()){
            redisTemplate.opsForValue().set(USER_FRIEND_APPLICATION + userId, Collections.emptyList(),20, TimeUnit.MINUTES);
        }else {
            redisTemplate.opsForValue().set(USER_FRIEND_APPLICATION + userId, list,20, TimeUnit.MINUTES);
        }
        return list;
    }

    @Override
    public int countByUserIdWithinOneMonthUsing(Integer userId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.MONTHS);
        LambdaQueryWrapper<UserContact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserContact::getRequesterId, userId)
                .ge(UserContact::getCreateTime, startDate)
                .le(UserContact::getCreateTime, endDate);
        return mapper.selectCount(queryWrapper);
    }

    @Override
    public int countByUserIdObtainedOneMonth(Integer userId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.MONTHS);
        LambdaQueryWrapper<UserContact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserContact::getRequesterId, userId)
                .ge(UserContact::getCreateTime, startDate)
                .le(UserContact::getCreateTime, endDate);
        return mapper.selectCount(queryWrapper);
    }
}




