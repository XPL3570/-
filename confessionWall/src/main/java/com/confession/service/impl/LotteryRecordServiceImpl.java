package com.confession.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.Strategy;
import com.confession.config.WallConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.LotteryMapper;
import com.confession.mapper.LotteryRecordMapper;
import com.confession.pojo.Lottery;
import com.confession.pojo.LotteryRecord;
import com.confession.service.LotteryRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.confession.comm.ResultCodeEnum.WITHDRAWAL_EXCEEDS_LIMIT;

@Service
public class LotteryRecordServiceImpl extends ServiceImpl<LotteryRecordMapper, LotteryRecord> implements LotteryRecordService {

    @Resource
    private LotteryMapper lotteryMapper;
    @Resource
    private LotteryRecordMapper lotteryRecordMapper;

    @Resource
    private WallConfig wallConfig;


    @Override
    @Transactional
    public Lottery extractTape(Integer schoolId, Integer gender, Integer userId) {
        //校验拿到的纸条是否超过限制
        checkStrategy(userId);

        //todo 这里可以先查询一遍还有没有自己没有拿到过的纸条

        Lottery lottery = getRandomLotteryByGenderAndSchoolIdAndDrawCount(gender, schoolId, 0);
        // 创建一个新的 LotteryRecord 对象
        LotteryRecord record = new LotteryRecord();
        record.setLotteryId(lottery.getId());
        record.setUserId(userId);
        // 插入记录
        lotteryRecordMapper.insert(record);
        return lottery;
    }

    @Override
    public void obtainedNote(Integer userId) {
        LambdaQueryWrapper<LotteryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LotteryRecord::getUserId, userId)
                .ge(LotteryRecord::getDrawAt, LocalDateTime.now().minusMonths(3))
                .orderByDesc(LotteryRecord::getDrawAt);
        Page<LotteryRecord> page = new Page<>(1, 20);
        List<LotteryRecord> records = lotteryRecordMapper.selectPage(page, queryWrapper).getRecords();
        //todo  明天写这 遍历去拿到纸条记录表里面的内容
    }


    //todo 这里要把自己拿到过的记录排除了
    private synchronized Lottery getRandomLotteryByGenderAndSchoolIdAndDrawCount(int gender, int schoolId, int drawCount) {
        // 构建LambdaQueryWrapper
        LambdaQueryWrapper<Lottery> queryWrapper = new LambdaQueryWrapper<Lottery>()
                .eq(Lottery::getGender, gender)
                .eq(Lottery::getSchoolId, schoolId)
                .eq(Lottery::getDrawCount, drawCount);

        // 使用行级锁查询符合条件的记录
        List<Lottery> lotteryList = lotteryMapper.selectList(queryWrapper);

        if (lotteryList.isEmpty()) {
            // 如果没有满足条件的记录，则递归调用自身，将drawCount加1
            return getRandomLotteryByGenderAndSchoolIdAndDrawCount(gender, schoolId, drawCount + 1);
        } else {
            // 随机选择一条记录
            Random random = new Random();
            int randomIndex = random.nextInt(lotteryList.size());
            Lottery randomLottery = lotteryList.get(randomIndex);

            // 更新DrawCount的值
            randomLottery.setDrawCount(randomLottery.getDrawCount() + 1);
            lotteryMapper.updateById(randomLottery);

            return randomLottery;
        }
    }

    /**
     * 检查策略 和校验
     *
     * @param userId
     */
    private void checkStrategy(Integer userId) {
        // 将配置文件中的字符串转换为枚举值
        Strategy strategy = Strategy.valueOf(wallConfig.getStrategyString().toUpperCase());

        // 根据策略进行检查
        switch (strategy) {
            case YEARLY:
                // 查询过去一年该用户是否已经抽过奖
                int yearlyCount = lotteryRecordMapper.countYearlyRecords(userId);
                if (yearlyCount >= wallConfig.getYearlyLimit()) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
            case MONTHLY:
                // 查询过去一个月该用户是否已经抽过奖
                int monthlyCount = lotteryRecordMapper.countMonthlyRecords(userId);
                if (monthlyCount >= wallConfig.getMonthlyLimit()) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
            case DAILY:
                // 查询过去一天该用户是否已经抽过奖
                int dailyCount = lotteryRecordMapper.countDailyRecords(userId);
                if (dailyCount >= wallConfig.getDailyLimit()) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
            case ONCE:
                // 查询该用户是否已经抽过奖
                int totalCount = lotteryRecordMapper.countTotalRecords(userId);
                if (totalCount >= wallConfig.getTotalLimit()) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
        }
    }

}
