package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.pojo.Admin;
import com.confession.request.AddWallAdminRequest;
import com.confession.request.AdminLoginRequest;
import com.confession.request.AdminModPwdRequest;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
public interface AdminService extends IService<Admin> {

    /**
     *  判断是不是该墙的管理员
     * @param userId
     * @param wallId
     * @return
     */
    boolean isAdmin(Integer userId, Integer wallId);


    /**
     * 超级管理员登录
     */
    Map login(AdminLoginRequest adminLoginRequest);

    /**
     * 超级管理员修改密码
     */
    void modifyPwd(AdminModPwdRequest request);

    /**
     * 添加表白墙管理员         注意数据库加了唯一索引，用户id学校id和表白墙id三个字段
     */
    void adminService(AddWallAdminRequest request);
}
