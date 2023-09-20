package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.dto.UserDTO;
import com.confession.pojo.User;
import com.confession.request.UserStatusModRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
public interface UserService extends IService<User> {

    /**
     * 查询数据库的Openid的用户
     *
     * @param openid
     * @return
     */
    User findByOpenid(String openid);

    /**
     * @param code
     * @return
     */
    String codeByOpenid(String code);

    /**
     * 根据id查询用户名和头像封装的DTO
     * @return 一个集合
     */
    List<UserDTO> getUsersFromRedisOrDatabase(List<Integer> userIds);

    /**
     * 根据id查询用户名和头像封装的DTO
     * @param userId
     * @return
     */
    UserDTO getUserFromRedisOrDatabase(Integer userId);


    /**
     * 修改用户状态
     * @param userStatusModRequest
     */
    void statusMod(UserStatusModRequest userStatusModRequest);
}
