package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.UserDTO;
import com.confession.dto.UserManageDTO;
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

    /**
     * 根据条件查询用户
     * @param pageTool
     * @param schoolName 学校名字 模糊查询
     * @param userName  用户名 模糊查询
     * @param status   状态，如果输入1表示状态异常的用户，没有表示所有用户
     * @return
     */
    PageResult selectUserList(PageTool pageTool, String schoolName, String userName, Integer status);
}
