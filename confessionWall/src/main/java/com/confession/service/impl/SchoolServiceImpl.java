package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.IndexInfoDTO;
import com.confession.dto.SchoolApplicationDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.*;
import com.confession.pojo.*;
import com.confession.request.RegisterSchoolRequest;
import com.confession.request.SchoolExamineRequest;
import com.confession.service.SchoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Arrays;
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
    private SchoolApplicationMapper schoolApplicationMapper;

    @Resource
    private MsgConfigurationMapper msgConfigurationMapper;

    @Resource
    private ConfessionwallMapper confessionwallMapper;

    @Resource
    private AdminMapper adminMapper;



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
        //这里那个消息表只能存放一条记录，就有问题
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
    public PageResult viewSchool(PageTool pageTool) {
        Page<School> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<School> schools = schoolMapper.selectPage(page, null).getRecords();
        long total = page.getTotal();
        return new PageResult(schools, total,schools.size());
    }

    @Override
    public PageResult viewNoReview(PageTool pageTool) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getIsVerified,0);
        Page<School> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<School> schools = schoolMapper.selectPage(page, wrapper).getRecords();

        // 转换为DTO
        List<SchoolApplicationDTO> dtoList = schools.stream().map(school -> {
            SchoolApplicationDTO dto = new SchoolApplicationDTO();
            dto.setSchoolId(school.getId());
            dto.setSchoolName(school.getSchoolName());
            dto.setAvatarURL(school.getAvatarURL());
            dto.setDescription(school.getDescription());
            dto.setCreateTime(school.getCreateTime());
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

        return new PageResult<>(dtoList,page.getTotal(),schools.size());
    }


    @Override
    @Transactional
    public void examinePost(SchoolExamineRequest schoolExamineRequest) {
        LambdaUpdateWrapper<School> updateWrapper = new LambdaUpdateWrapper<>();  //学校
        updateWrapper.eq(School::getId, schoolExamineRequest.getSchoolId())
                .set(School::getIsVerified,schoolExamineRequest.getIsVerified());

        LambdaUpdateWrapper<SchoolApplication> updateWrapperZj = new LambdaUpdateWrapper<>(); //记录
        updateWrapperZj.eq(SchoolApplication::getSchoolId,schoolExamineRequest.getSchoolId()).
                set(SchoolApplication::getIsApproved,JwtInterceptor.getUser().getId())
                .set(SchoolApplication::getIsApproved,schoolExamineRequest.getIsVerified());



        School school = schoolMapper.selectById(schoolExamineRequest.getSchoolId());
        
        //如果通过了，把用户的学校id，对应表白墙的状态，还有添加一个管理员账号
        if (schoolExamineRequest.getIsVerified()==1){
            LambdaUpdateWrapper<User> userWrapper = new LambdaUpdateWrapper<>();//用户
            userWrapper.eq(User::getId,school.getCreatorId())
                    .set(User::getSchoolId,school.getId());
            userMapper.update(null,userWrapper);

            LambdaUpdateWrapper<Confessionwall> updateWrapperWall = new LambdaUpdateWrapper<>(); //表白墙
            updateWrapperWall.eq(Confessionwall::getSchoolId,schoolExamineRequest.getSchoolId())
                    .set(Confessionwall::getStatus,0); //0是设置成正常
            confessionwallMapper.update(null,updateWrapperWall);

            LambdaQueryWrapper<SchoolApplication> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SchoolApplication::getSchoolId,schoolExamineRequest.getSchoolId());
            //这里只需要查询一个，因为一个学校也只能同时申请一个，这里也只存了一个
            SchoolApplication schoolInfo = schoolApplicationMapper.selectOne(wrapper);
            Admin admin = new Admin();
            admin.setSchoolId(schoolExamineRequest.getSchoolId());
            admin.setPhoneNumber(schoolInfo.getPhoneNumber());
            admin.setWeChatId(schoolInfo.getWechatNumber());
            admin.setUserId(school.getCreatorId());
            Confessionwall confessionwall = confessionwallMapper.selectOne(new LambdaQueryWrapper<Confessionwall>()
                    .eq(Confessionwall::getSchoolId,schoolExamineRequest.getSchoolId()));
            admin.setConfessionWallId(confessionwall.getId());
            admin.setPermission(0); //普通管理
            adminMapper.insert(admin);
        }

        schoolApplicationMapper.update(null, updateWrapperZj);
        int update = schoolMapper.update(null, updateWrapper);
        if (update<1){
            throw new WallException("学校状态或学校记录表状态修改异常",201);
        }
    }

    @Override
    public List<Integer> selectIdsByNameLike(String schoolName) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(School::getSchoolName,schoolName);
        return schoolMapper.selectList(wrapper.like(School::getSchoolName,schoolName)).stream().map(School::getId).collect(Collectors.toList());

    }

    @Override
    public String getSchoolNameById(Integer schoolId) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getId, schoolId);
        School school = this.getOne(wrapper);
        return school != null ? school.getSchoolName() : null;
    }

    @Override
    public IndexInfoDTO getIndexInfo(Integer schoolId) {
        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getId, schoolId);
        School school = this.getOne(wrapper);
        //轮播图 todo 可能要加表，来保存设置所有人可以看到的轮播图
        List<String> imageList = Arrays.asList(school.getCarouselImages().split(";"));
        IndexInfoDTO dto = new IndexInfoDTO();
        dto.setCarouselImages(imageList);
        String promptMessage = this.getPromptMessage(schoolId);
        dto.setPrompt(promptMessage);
        return dto;
    }

}
