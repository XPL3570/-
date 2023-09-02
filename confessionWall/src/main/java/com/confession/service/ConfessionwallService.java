package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.pojo.Confessionwall;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
public interface ConfessionwallService extends IService<Confessionwall> {

    /**
     * 查询学校id下的的一个墙id，这里如果是多个就返回第一个
     * @param schoolId
     * @return
     */
    Integer selectSchoolInWallIdOne(Integer schoolId);

}
