package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.confession.comm.EncryptionUtil;
import com.confession.config.JwtConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.AdminMapper;
import com.confession.pojo.Admin;
import com.confession.request.AdminLoginRequest;
import com.confession.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
    @Resource
    private AdminMapper adminMapper;


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

    @Override
    public Map login(AdminLoginRequest adminLoginRequest) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        //这里那字段里面的微信号做账号
        wrapper.eq(Admin::getWeChatId,adminLoginRequest.getAccount());
        try {
            wrapper.eq(Admin::getPassword, EncryptionUtil.encrypt(adminLoginRequest.getPassword()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println(EncryptionUtil.encrypt(adminLoginRequest.getPassword()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wrapper.eq(Admin::getPermission,1);
        Admin admin = adminMapper.selectOne(wrapper);
        Map<String, Object> map = new HashMap<>();
        String token;
        if (admin!=null){
            admin.setPassword("");
            map.put("admin",admin);
            token = JwtConfig.getAdminJwtToken(admin);
            if (token!=null&&token!=""){
                map.put("token",token);
            }else {
                throw new WallException("token生产失败",201);
            }
        }else {
            throw new WallException("账号或密码错误",201);
        }
        return map;


    }
}
