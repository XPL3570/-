package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.pojo.Confessionwall;
import com.confession.request.RegistryWhiteWallRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
public interface ConfessionwallService extends IService<Confessionwall> {

    /**
     * 查询学校id下的的一个墙id，这里如果是多个就返回第一个  ,状态要正常
     * @param schoolId
     * @return
     */
    Confessionwall selectSchoolInWallOne(Integer schoolId);

    /**
     * 注册表白墙，注册学校之后拿着id来注册表白墙
     */
    void register(RegistryWhiteWallRequest registryWhiteWallRequest);


    /**
     * 管理员查看表白墙列表
     * @param pageTool
     * @return
     */
    PageResult wallList(PageTool pageTool);

}
