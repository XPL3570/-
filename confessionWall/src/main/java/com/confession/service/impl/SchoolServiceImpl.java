package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.confession.comm.PageTool;
import com.confession.dto.SchoolApplicationDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.MsgConfigurationMapper;
import com.confession.mapper.SchoolApplicationMapper;
import com.confession.mapper.SchoolMapper;
import com.confession.mapper.UserMapper;
import com.confession.pojo.MsgConfiguration;
import com.confession.pojo.School;
import com.confession.pojo.SchoolApplication;
import com.confession.pojo.User;
import com.confession.request.RegisterSchoolRequest;
import com.confession.request.SchoolExamineRequest;
import com.confession.service.SchoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static com.confession.comm.ResultCodeEnum.SCHOOL_REGISTERED;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private UserMapper userMapper;


    @Resource
    private MsgConfigurationMapper msgConfigurationMapper;

    @Resource
    private SchoolApplicationMapper schoolApplicationMapper;


    /**
     * 只能查询已经审核通过的学校，审核状态是不是通过的，也查询不到，
     * @param schoolName 学校名字
     * @return
     */
    @Override
    public School findBySchoolName(String schoolName) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getSchoolName,schoolName);
        wrapper.eq(School::getIsVerified,1);
        School school = schoolMapper.selectOne(wrapper);
        return school;
    }

    @Override
    public String getPromptMessage(Integer schoolId) {
        MsgConfiguration msgConfiguration = msgConfigurationMapper.selectOne(null);
        if (msgConfiguration.getMainSwitch()) {
            return msgConfiguration.getMessage();
        }
        School school = schoolMapper.selectById(schoolId);
        if (school.getPrompt()==null||school.getPrompt()==""){ //如果没有设置，就那设置的
            return msgConfiguration.getMessage();
        }
        return school.getPrompt();
    }

    @Override
    public Integer registerSchool(RegisterSchoolRequest registerSchool) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getSchoolName,registerSchool.getSchoolName());
        School school;
        try {
            school=schoolMapper.selectOne(wrapper); //查询到多个会报错
        }catch (Exception e){
            e.printStackTrace();
            throw new WallException(SCHOOL_REGISTERED);
        }
        if (school==null){ //没有注册
            school = new School();
            school.setCreatorId(registerSchool.getUserId());
            school.setSchoolName(registerSchool.getSchoolName());
            school.setAvatarURL(registerSchool.getAvatarURL());
            school.setDescription(registerSchool.getDescription());
            school.setIsVerified(0);//初始状态
            schoolMapper.insert(school);

            // 获取新插入的学校的ID
            Integer schoolId = school.getId();
            // 创建一个新的SchoolApplication对象
            SchoolApplication schoolApplication = new SchoolApplication();
            schoolApplication.setSchoolId(schoolId);
            schoolApplication.setWechatNumber(registerSchool.getWechatNumber());
            schoolApplication.setPhoneNumber(registerSchool.getPhoneNumber());

            schoolApplication.setIsApproved(0);

        // 插入新的SchoolApplication记录
            schoolApplicationMapper.insert(schoolApplication);

            return schoolId; // 返回新插入的记录的ID
        }else {
            throw new WallException(SCHOOL_REGISTERED);
        }
    }


    @Override
    public List<School> viewSchool(PageTool pageTool) {
        Page<School> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<School> schools = schoolMapper.selectPage(page, null).getRecords();
        return schools;
    }

    @Override
    public List<SchoolApplicationDTO> viewNoReview(PageTool pageTool) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getIsVerified,0);
        Page<School> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<School> schools = schoolMapper.selectPage(page, null).getRecords();

        // 转换为DTO
        List<SchoolApplicationDTO> dtoList = schools.stream().map(school -> {
            SchoolApplicationDTO dto = new SchoolApplicationDTO();
            dto.setSchoolId(school.getId());
            dto.setSchoolName(school.getSchoolName());
            dto.setAvatarURL(school.getAvatarURL());
            dto.setDescription(school.getDescription());
            User user = userMapper.selectById(school.getCreatorId());
            dto.setCreatorUsername(user.getUsername());
            dto.setCreatorUserAvatarURL(user.getAvatarURL());
            // 查询对应的申请信息
            SchoolApplication application = schoolApplicationMapper.selectOne(
                    new LambdaQueryWrapper<SchoolApplication>().eq(SchoolApplication::getSchoolId, school.getId()));
            if (application != null) {
                dto.setWechatNumber(application.getWechatNumber());
                dto.setPhoneNumber(application.getPhoneNumber());
            }
            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }


    @Override
    public void examinePost(SchoolExamineRequest schoolExamineRequest) {
        School school = new School();
        school.setIsVerified(schoolExamineRequest.getIsVerified());

        LambdaUpdateWrapper<School> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(School::getId, schoolExamineRequest.getSchoolId());

        int update = schoolMapper.update(school, updateWrapper);
        if (update<1){
            throw new WallException("修改失败，更新数小于一条",201);
        }
    }

}
