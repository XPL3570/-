package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.MsgConfigurationMapper;
import com.confession.mapper.SchoolMapper;
import com.confession.pojo.MsgConfiguration;
import com.confession.pojo.School;
import com.confession.request.RegisterSchoolRequest;
import com.confession.service.SchoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    private MsgConfigurationMapper msgConfigurationMapper;


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
    public void register(RegisterSchoolRequest registerSchool) {
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
            school.setSchoolName(registerSchool.getSchoolName());
            school.setAvatarURL(registerSchool.getAvatarURL());
            school.setDescription(registerSchool.getDescription());
            schoolMapper.insert(school);
        }else {
            throw new WallException(SCHOOL_REGISTERED);
        }


    }

}
