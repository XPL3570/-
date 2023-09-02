package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.pojo.Confessionwall;
import com.confession.service.ConfessionwallService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static com.confession.comm.ResultCodeEnum.DATA_ERROR;

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
    public Integer selectSchoolInWallIdOne(Integer schoolId) {
        LambdaQueryWrapper<Confessionwall> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Confessionwall::getSchoolId,schoolId);
        List<Confessionwall> wallList = confessionwallMapper.selectList(wrapper);
        if (wallList!=null){
            return wallList.get(0).getId(); //拿到第一个墙id
        }else {
            throw new WallException(DATA_ERROR);
        }
    }
}
