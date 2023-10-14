package com.confession.service;

import com.confession.comm.Result;
import com.confession.pojo.MsgConfiguration;
import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.request.MsgGlobalPromptRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年09月14日
 */
public interface MsgConfigurationService extends IService<MsgConfiguration> {

    /**
     * 获取全局提示语
     */
    MsgConfiguration getGlobalPrompt();

    /**
     * 设置全局提示语，参数：开关和提示语
     */
    void setGlobalPrompts(MsgGlobalPromptRequest request);

}
