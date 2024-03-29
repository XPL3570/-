package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.dto.UserDTO;
import com.confession.dto.UserManageDTO;
import com.confession.pojo.User;
import com.confession.request.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
public interface UserService extends IService<User> {

    /**
     * 查询数据库的Openid的用户
     * @param openid
     */
    User findByOpenid(String openid);

    /**
     * 登录
     */
    Result login(LoginRequest request);


    /**
     * 注册接口
     */
    Result register(RegisterRequest request);

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

    /**
     * 根据条件查询用户
     * @param pageTool
     * @param schoolName 学校名字 模糊查询
     * @param userName  用户名 模糊查询
     * @param status   状态，如果输入1表示状态异常的用户，没有表示所有用户
     * @return
     */
    PageResult selectUserList(PageTool pageTool, String schoolName, String userName, Integer status);

    /**
     * 修改用户名字    这是超级管理员的接口
     * @param nameModRequest
     */
    void userMod(UserNameModRequest nameModRequest);

    /**
     * 普通用户修改用户名或者头像
     */
    void updateUserAttribute(String attributeName, String attributeValue);

    /**
     * 用户1天内是否修改头像或者名字
     */
    boolean checkTimeInterval(LocalDateTime lastUpdateTime);

    /**
     * 更新用户的联系方式和可获取的状态
     * @param request
     */
    void updateWeChat(UserWeChatModRequest request);

    /**
     * 用户是否可以删除 ，用redis计数，每12小时2条  ,超过限制抛异常，没有就累加1
     */
    void userCanDelete(Integer userId);
}
