package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        if (this.countByUserIdWithinOneMonthUsing(userId)>=3){
            throw new WallException(FREQUENT_USER_ACCESS_WX);
        }
        if (this.countByUserIdObtainedOneMonth(request.getReceiverId())>=7){ //被获取方月被获取次数是7次 todo 改到配置文件
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
    }

    @Override
    public void setAgreeOrNot(AgreeSetContactRequest request) {
        Integer userId = JwtInterceptor.getUser().getId();
        UserContact contact = mapper.selectById(request.getUserContactId());
        if (contact==null||!contact.getRequesterId().equals(userId)){
            throw new WallException(FAIL); //这里是想修改的id不对，直接报错
        }
        if (request.getIsAgree()){
            contact.setStatus(1);
        }else {
            contact.setStatus(2);
        }
        this.save(contact);
    }


    @Override
    public List<UserContactDTO> youApplicationSent() {
        List<UserContactDTO> list = mapper.findApplicationSentContactList(JwtInterceptor.getUser().getId());
        return list;
    }


    @Override
    public List<UserContactDTO> getYourOwnContact() {
        List<UserContactDTO> list = mapper.findGetMeContactList(JwtInterceptor.getUser().getId());
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




