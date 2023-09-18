package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.mapper.MsgConfigurationMapper;
import com.confession.mapper.SchoolMapper;
import com.confession.pojo.MsgConfiguration;
import com.confession.pojo.School;
import com.confession.service.SchoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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



    @Override
    public School findBySchoolName(String schoolName) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getSchoolName,schoolName);
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
        return school.getPrompt();
    }

}
