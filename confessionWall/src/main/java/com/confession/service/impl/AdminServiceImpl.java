package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.confession.mapper.AdminMapper;
import com.confession.pojo.Admin;
import com.confession.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {


    @Override
    public boolean isAdmin(Integer userId, Integer wallId) {
        // 查询用户是否是超级管理员
        LambdaQueryWrapper<Admin> superAdminQuery = Wrappers.<Admin>lambdaQuery()
                .eq(Admin::getUserId, userId)
                .eq(Admin::getPermission, 1);
        int superAdminCount = getBaseMapper().selectCount(superAdminQuery);
        if (superAdminCount > 0) {
            return true;
        }

        // 查询用户是否是该表白墙的管理员
        LambdaQueryWrapper<Admin> wallAdminQuery = Wrappers.<Admin>lambdaQuery()
                .eq(Admin::getUserId, userId)
                .eq(Admin::getConfessionWallId, wallId);
        int wallAdminCount = getBaseMapper().selectCount(wallAdminQuery);
        if (wallAdminCount > 0) {
            return true;
        }
        return false;
    }
}
