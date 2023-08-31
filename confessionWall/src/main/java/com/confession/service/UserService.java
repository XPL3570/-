package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.pojo.User;

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
     * 通过
     * @param code
     * @return
     */
    String codeByOpenid(String code);


}
