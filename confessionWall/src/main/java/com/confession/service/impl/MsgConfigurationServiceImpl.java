package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.confession.globalConfig.exception.WallException;
import com.confession.pojo.MsgConfiguration;
import com.confession.mapper.MsgConfigurationMapper;
import com.confession.request.MsgGlobalPromptRequest;
import com.confession.service.MsgConfigurationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年09月14日
 */
@Service
public class MsgConfigurationServiceImpl extends ServiceImpl<MsgConfigurationMapper, MsgConfiguration> implements MsgConfigurationService {
    @Resource
    private MsgConfigurationMapper mapper;
    @Override
    public MsgConfiguration getGlobalPrompt(){
        QueryWrapper<MsgConfiguration> wrapper = new QueryWrapper<>();
        wrapper.last("LIMIT 1"); // 只查询一条数据
        return  mapper.selectOne(wrapper);
    }

    @Override
    public void setGlobalPrompts(MsgGlobalPromptRequest request) {
        QueryWrapper<MsgConfiguration> wrapper = new QueryWrapper<>();
        wrapper.last("LIMIT 1"); // 只查询一条数据

        MsgConfiguration updateMsg = new MsgConfiguration();
        updateMsg.setMessage(request.getMessage());
        updateMsg.setMainSwitch(request.getMainSwitch());

        int update = mapper.update(updateMsg, wrapper);
        if (update<1){
            throw new WallException("修改失败",201);
        }

    }
}
