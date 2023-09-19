package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.pojo.School;
import com.confession.request.RegisterSchoolRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
public interface SchoolService extends IService<School> {

    /**
     * 查询学校 需要时审核通过的
     * @param schoolName 学校名字
     * @return
     */
    School findBySchoolName(String schoolName);

    /**
     * 拿到提示语，后台可以统一控制是否拿到学校的提示语，所以先读取设置提示语的表
     * @param schoolId
     * @return
     */
    String getPromptMessage(Integer schoolId);

    /**
     * 注册学校
     */
    void register(RegisterSchoolRequest registerSchool);
}
