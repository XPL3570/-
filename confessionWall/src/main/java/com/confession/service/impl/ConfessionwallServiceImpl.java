package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.WallDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.mapper.SchoolMapper;
import com.confession.pojo.Confessionwall;
import com.confession.pojo.School;
import com.confession.request.RegistryWhiteWallRequest;
import com.confession.service.ConfessionwallService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static com.confession.comm.ResultCodeEnum.DATA_ERROR;
import static com.confession.comm.ResultCodeEnum.FAIL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class ConfessionwallServiceImpl extends ServiceImpl<ConfessionwallMapper, Confessionwall> implements ConfessionwallService {
    @Resource
    private ConfessionwallMapper confessionwallMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Override
    @Cacheable(value = "wallUnderSchool#12#h", key = "#schoolId")
    public Confessionwall selectSchoolInWallOne(Integer schoolId) {
        LambdaQueryWrapper<Confessionwall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Confessionwall::getSchoolId,schoolId);
        wrapper.eq(Confessionwall::getStatus,0); //状态
        List<Confessionwall> wallList = confessionwallMapper.selectList(wrapper);
        if (wallList!=null){
            return wallList.get(0); //拿到第一个墙id
        }else {
            throw new WallException(DATA_ERROR);
        }
    }

    @Override
    public void register(RegistryWhiteWallRequest registryWhiteWallRequest) {
        School school = schoolMapper.selectById(registryWhiteWallRequest.getSchoolId());
        if (school==null){
            throw new WallException(FAIL);
        }
        System.out.println(JwtInterceptor.getUser());
        Confessionwall zj = new Confessionwall();
        zj.setSchoolId(registryWhiteWallRequest.getSchoolId());
        zj.setAvatarURL(registryWhiteWallRequest.getAvatarURL());
        zj.setWallName(registryWhiteWallRequest.getConfessionWallName());
        zj.setCreatorUserId( JwtInterceptor.getUser().getId());
        zj.setDescription(registryWhiteWallRequest.getDescription());
        zj.setStatus(1); //默认禁用
        confessionwallMapper.insert(zj);
    }

    @Override
    public PageResult wallList(PageTool pageTool) {
        Page<Confessionwall> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<Confessionwall> list = this.page(page).getRecords();
        List<WallDTO> wallDtoS = list.stream().map(
                this::toWallDTO
        ).collect(Collectors.toList());
        return new PageResult<>(wallDtoS,page.getTotal(),wallDtoS.size());
    }

    private WallDTO toWallDTO(Confessionwall wall){
        WallDTO wallDTO = new WallDTO();

        // 将实体类的属性逐个赋值给 DTO 对象
        wallDTO.setId(wall.getId());
        wallDTO.setSchoolId(wall.getSchoolId());
        wallDTO.setAvatarURL(wall.getAvatarURL());
        wallDTO.setWallName(wall.getWallName());
        wallDTO.setDescription(wall.getDescription());
        wallDTO.setCreateTime(wall.getCreateTime());
        wallDTO.setStatus(wall.getStatus());

        // 使用 schoolMapper 查询学校名字，并将其赋值给 DTO 对象
        School school = schoolMapper.selectById(wall.getSchoolId());
        if (school != null) {
            wallDTO.setSchoolName(school.getSchoolName());
        }

        return wallDTO;


    }
}
