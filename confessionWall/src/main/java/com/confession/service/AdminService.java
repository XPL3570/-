package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.pojo.Admin;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
public interface AdminService extends IService<Admin> {

    /**
     *  判断是不是该墙的管理员，或者超级管理员直接通过
     * @param userId
     * @param wallId
     * @return
     */
    boolean isAdmin(Integer userId, Integer wallId);
}
