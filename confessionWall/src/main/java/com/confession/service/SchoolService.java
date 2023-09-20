package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageTool;
import com.confession.pojo.School;
import com.confession.request.RegisterSchoolRequest;
import com.confession.request.SchoolExamineRequest;

import java.util.List;

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
    Integer register(RegisterSchoolRequest registerSchool);

    /**
     * 查看学校
     * @param pageTool
     * @return
     */
    List<School> viewSchool(PageTool pageTool);

    /**
     * 查看没有通过审核的接口
     * @param pageTool
     * @return
     */
    List<School> viewNoReview(PageTool pageTool);


    /**
     * 审核学校
     * @param schoolExamineRequest
     */
    void examinePost(SchoolExamineRequest schoolExamineRequest);
}
