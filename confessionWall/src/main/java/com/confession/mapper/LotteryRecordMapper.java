package com.confession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.confession.pojo.LotteryRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LotteryRecordMapper extends BaseMapper<LotteryRecord> {

    int countDailyRecords(Integer userId);
    int countMonthlyRecords(Integer userId);
    int countYearlyRecords(Integer userId);
    int countTotalRecords(Integer userId);

}
