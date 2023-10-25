package com.confession.mapper;

import com.confession.dto.UserContactDTO;
import com.confession.pojo.UserContact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.confession.pojo.UserContact
 */
@Mapper
public interface UserContactMapper extends BaseMapper<UserContact> {

    /**
     * 查询用户发起获取联系方式列表 最长20条
     */
    List<UserContactDTO> findApplicationSentContactList(int requesterId);

    /**
     * 获取我的联系方式的请求 最长20条
     */
    List<UserContactDTO> findGetMeContactList(int receiverId);

}




