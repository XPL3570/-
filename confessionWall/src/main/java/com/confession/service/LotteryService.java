package com.confession.service;

import com.confession.pojo.Lottery;
import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.request.LotteryRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年09月16日
 */
public interface LotteryService extends IService<Lottery> {

    /**
     * 放入纸条
     */
    boolean insert(LotteryRequest request,Integer userId);


    /**
     * 获取用户投入的纸条 最多二十条，不做分页
     *
     * @param userId
     * @return
     */
    List<Lottery> postedNote(Integer userId);

}
