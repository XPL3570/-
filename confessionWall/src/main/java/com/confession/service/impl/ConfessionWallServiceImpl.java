package com.confession.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.WallDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.mapper.SchoolMapper;
import com.confession.pojo.ConfessionWall;
import com.confession.pojo.School;
import com.confession.request.ModifyWallRequest;
import com.confession.request.RegistryWhiteWallRequest;
import com.confession.service.ConfessionWallService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.confession.comm.RedisConstant.WALL_UNDER_SCHOOL;
import static com.confession.comm.ResultCodeEnum.DATA_ERROR;
import static com.confession.comm.ResultCodeEnum.FAIL;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@Service
public class ConfessionWallServiceImpl extends ServiceImpl<ConfessionwallMapper, ConfessionWall> implements ConfessionWallService {
    @Resource
    private ConfessionwallMapper confessionwallMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public ConfessionWall selectSchoolInWallOne(Integer schoolId) {
        String cacheKey = WALL_UNDER_SCHOOL + schoolId; //学校对应的墙信息
        JSONObject jsonObject = (JSONObject) redisTemplate.opsForValue().get(cacheKey);
        ConfessionWall confessionwall;
        if (jsonObject != null) {
            confessionwall = jsonObject.toJavaObject(ConfessionWall.class);
            return confessionwall;
        } else {
            LambdaQueryWrapper<ConfessionWall> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ConfessionWall::getSchoolId, schoolId);
//            wrapper.eq(Confessionwall::getStatus, 0); // 状态 这里的状态就不要了，被禁用的状态也可以，反正被禁用了就发布不了
            List<ConfessionWall> wallList = confessionwallMapper.selectList(wrapper);
            if (wallList != null && !wallList.isEmpty()) {
                confessionwall = wallList.get(0);
                redisTemplate.opsForValue().set(cacheKey, confessionwall, 2 * 24 * 60 * 60, TimeUnit.SECONDS); // 缓存2天
                return confessionwall;
            } else {
                throw new WallException(DATA_ERROR);
            }
        }
    }

    @Override
    public void register(RegistryWhiteWallRequest registryWhiteWallRequest) {
        School school = schoolMapper.selectById(registryWhiteWallRequest.getSchoolId());
        if (school == null) {
            throw new WallException(FAIL);
        }
//        System.out.println(JwtInterceptor.getUser());
        ConfessionWall zj = new ConfessionWall();
        zj.setSchoolId(registryWhiteWallRequest.getSchoolId());
        zj.setAvatarURL(registryWhiteWallRequest.getAvatarURL());
        zj.setWallName(registryWhiteWallRequest.getConfessionWallName());
        zj.setCreatorUserId(JwtInterceptor.getUser().getId());
        zj.setDescription(registryWhiteWallRequest.getDescription());
        zj.setStatus(true); //默认禁用
        confessionwallMapper.insert(zj);
    }

    @Override
    public PageResult wallList(PageTool pageTool) {
        Page<ConfessionWall> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<ConfessionWall> list = this.page(page).getRecords();
        List<WallDTO> wallDtoS = list.stream().map(
                this::toWallDTO
        ).collect(Collectors.toList());
        return new PageResult<>(wallDtoS, page.getTotal(), wallDtoS.size());
    }

    @Override
    public List<Integer> getAvailableWallsIds() {
        // 创建LambdaQueryWrapper对象
        LambdaQueryWrapper<ConfessionWall> queryWrapper = Wrappers.lambdaQuery();
        //状态是禁用了也可以，就不要了，这里目前只有删除缓存的时候再用这个接口 ，下面一行注释
//        queryWrapper.eq(Confessionwall::getStatus, 0);
        queryWrapper.select(ConfessionWall::getId);
        // 执行查询
        List<ConfessionWall> list = confessionwallMapper.selectList(queryWrapper);
        return list.stream().map(item->item.getId()).collect(Collectors.toList());
    }

    @Override
    public void modifyWall(ModifyWallRequest request) {
        ConfessionWall wall = new ConfessionWall();
        wall.setId(request.getWallId());
        wall.setWallName(request.getWallName());
        wall.setStatus(request.getStatus());
        wall.setDescription(request.getDescription());
        confessionwallMapper.updateById(wall);
    }

    private WallDTO toWallDTO(ConfessionWall wall) {
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
