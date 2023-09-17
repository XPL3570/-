package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.pojo.Lottery;
import com.confession.pojo.LotteryRecord;

public interface LotteryRecordService extends IService<LotteryRecord> {

    /**
     * 从学校的纸箱拿到纸条
     * @param schoolId
     * @return
     */
    Lottery extractTape(Integer schoolId, Integer gender,Integer userId);

    /**
     * 获取用户抽到的纸条  最长三个月 - 20条
     * @param userId
     */
    void obtainedNote(Integer userId);
}
