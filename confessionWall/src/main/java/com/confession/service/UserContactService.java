package com.confession.service;

import com.confession.dto.UserContactDTO;
import com.confession.pojo.UserContact;
import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.request.AgreeSetContactRequest;
import com.confession.request.ObtainContactInfoRequest;

import java.util.List;

/**
 *
 */
public interface UserContactService extends IService<UserContact> {

    /**
     * 获取有多少人获取自己联系方式(自己还没同意的)
     */
    int getYourOwnContactInfo();

    /**
     * 用户获取其他用户联系联系方式的申请
     * @param request 被获取方用户id和申请理由
     */
    void obtainContact(ObtainContactInfoRequest request);


    /**
     * 设置改条申请是否同意
     * @param request 表id和同意状态
     */
    void setAgreeOrNot(AgreeSetContactRequest request);

    /**
     *  获取自己联系方式的请求 这里不分页 最多20条
     */
    List<UserContactDTO> getYourOwnContact();

    /**
     *  查看自己发送的申请 这里不分页 最多20条
     */
    List<UserContactDTO> youApplicationSent();

    /**
     * 获取用户当月发起获取微信联系方式的数量
     */
    int countByUserIdWithinOneMonthUsing(Integer userId);

    /**
     * 被其他用户获取联系方式的count
     */
    int countByUserIdObtainedOneMonth(Integer userId);

}
