package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.confession.comm.EncryptionUtil;
import com.confession.comm.ResultCodeEnum;
import com.confession.config.JwtConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.AdminMapper;
import com.confession.pojo.Admin;
import com.confession.request.AddWallAdminRequest;
import com.confession.request.AdminLoginRequest;
import com.confession.request.AdminModPwdRequest;
import com.confession.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Resource
    private AdminMapper adminMapper;


    @Override
    public boolean isAdmin(Integer userId, Integer wallId) {
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

    @Override
    public void modifyPwd(AdminModPwdRequest request) {
        String newPassword = request.getNewPassword();
        String originalPassword = request.getOriginalPassword();
        if (newPassword.equals(request.getNewPasswordConfirmation())){
            throw new WallException("两次输入的新密码不一致",201);
        }
        if (originalPassword.equals(newPassword)){
            throw new WallException("新老密码一致！！！",201);
        }
        Integer adminId = JwtInterceptor.getUser().getId();
        Admin admin = adminMapper.selectById(adminId);
        if (admin==null){
            throw new WallException(ResultCodeEnum.FAIL);
        }
        if (!admin.getPassword().equals(EncryptionUtil.encrypt(originalPassword))){
            throw new WallException("密码不对!",204);
        }
        admin.setPassword(newPassword);
        adminMapper.updateById(admin);
    }

    @Override
    public void adminService(AddWallAdminRequest request) {
        Admin admin = new Admin();
        admin.setUserId(request.getUserId());
        admin.setWeChatId(request.getWeChatId());
        admin.setSchoolId(request.getSchoolId());
        admin.setConfessionWallId(request.getWallId());
        admin.setPhoneNumber(request.getPhoneNum());
        int insert = adminMapper.insert(admin);
        if (insert<1){
            throw new WallException("写入普通管理员失败失败，",204);
        }
    }
}
