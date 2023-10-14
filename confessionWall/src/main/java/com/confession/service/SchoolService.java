package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.IndexInfoDTO;
import com.confession.dto.SchoolApplicationDTO;
import com.confession.pojo.School;
import com.confession.request.RegisterSchoolRequest;
import com.confession.request.SchoolExamineRequest;
import com.confession.request.SchoolModifyRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
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
     * 拿到提示语，后台可以统一控制是否拿到学校的提示语，所以先读取设置提示语的表  接口好像不用了
     * @param schoolId
     * @return
     */
    String getPromptMessage(Integer schoolId);

    /**
     * 注册学校   这里规定一个用户只能注册一个学校
     */
    Integer registerSchool(RegisterSchoolRequest registerSchool);

    /**
     * 查看学校
     * @param pageTool
     * @return
     */
    PageResult viewSchool(PageTool pageTool);

    /**
     * 查看没有通过审核的接口
     * @param pageTool
     * @return
     */
    PageResult viewNoReview(PageTool pageTool);


    /**
     * 审核学校    把对应的学校表，学校申请记录表，对应的表白墙的状态设置成正常
     * @param schoolExamineRequest
     */
    void examinePost(SchoolExamineRequest schoolExamineRequest);

    /**
     * 通过关键字来模糊查询学校
     * @param schoolName
     * @return  符合条件的id集合
     */
    List<Integer> selectIdsByNameLike(String schoolName);

    /**
     * 根据id查询学校名字
     * @param schoolId
     * @return
     */
    String getSchoolNameById(Integer schoolId);

    /**
     * 获取首页需要的信息
     * @param schoolId
     * @return IndexInfoDTO 目前只有提示语和首页轮播图信息
     */
    IndexInfoDTO getIndexInfo(Integer schoolId);

    /**
     * 修改学校
     * @param request
     */
    void modifySchool(SchoolModifyRequest request);

    /**
     * 清除所有学校的首页信息
     */
    void deleteAllSchoolHomepageCaches();
}
