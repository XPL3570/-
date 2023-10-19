package com.confession.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.Strategy;
import com.confession.config.WallConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.LotteryMapper;
import com.confession.mapper.LotteryRecordMapper;
import com.confession.mapper.SchoolMapper;
import com.confession.pojo.Lottery;
import com.confession.pojo.LotteryRecord;
import com.confession.service.LotteryRecordService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.confession.comm.RedisConstant.SCHOOL_EXTRACTION_LOCK;
import static com.confession.comm.RedisConstant.USER_OBTAINED_NODE;
import static com.confession.comm.ResultCodeEnum.*;

@Service
public class LotteryRecordServiceImpl extends ServiceImpl<LotteryRecordMapper, LotteryRecord> implements LotteryRecordService {

    @Resource
    private LotteryMapper lotteryMapper;
    @Resource
    private LotteryRecordMapper lotteryRecordMapper;

    @Resource
    private WallConfig wallConfig;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private SchoolMapper schoolMapper;


    @Override
    @Transactional
    public Lottery extractTape(Integer schoolId, Integer gender, Integer userId) {
        //校验拿到的纸条是否超过限制
        checkStrategy(schoolId,userId);

        //获取用户抽到过的集合id
        List<Integer> lotteryIds = getLotteryIdsByUserId(userId);

        Lottery lottery;
        //加分布式锁
        RLock lock = redissonClient.getLock(SCHOOL_EXTRACTION_LOCK + schoolId);
        boolean isLockAcquired = false;
        try {
            isLockAcquired = lock.tryLock(3, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                System.out.println("获取学校抽取锁失败，学校id："+schoolId);
                throw new RuntimeException("获取学校抽取锁失败");
            }
            lottery = getRandomLotteryByGenderAndSchoolIdAndDrawCount(gender, schoolId, 0, lotteryIds, userId);
        } catch (InterruptedException e) {
            throw new WallException(DATA_ERROR);
        } finally {
            if (isLockAcquired) {
                lock.unlock();
            }
        }
        // 创建一个新的 LotteryRecord 对象
        LotteryRecord record = new LotteryRecord();
        record.setLotteryId(lottery.getId());
        record.setUserId(userId);
        // 插入记录
        lotteryRecordMapper.insert(record);
        redisTemplate.delete(USER_OBTAINED_NODE + userId);
        return lottery;
    }

    @Override
    public List<Lottery> obtainedNote(Integer userId) {
        JSON json = (JSON) redisTemplate.opsForValue().get(USER_OBTAINED_NODE + userId);
        if (json != null) {
            return json.toJavaObject(List.class);
        }
        LambdaQueryWrapper<LotteryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LotteryRecord::getUserId, userId)
                .ge(LotteryRecord::getDrawAt, LocalDateTime.now().minusMonths(3))
                .orderByDesc(LotteryRecord::getDrawAt);
        Page<LotteryRecord> page = new Page<>(1, 20);
        List<LotteryRecord> records = lotteryRecordMapper.selectPage(page, queryWrapper).getRecords();
        List<Lottery> list = new ArrayList<>();
        for (LotteryRecord record : records) {
            Lottery lottery = lotteryMapper.selectById(record.getLotteryId());
            list.add(lottery);
        }
        redisTemplate.opsForValue().set(USER_OBTAINED_NODE + userId, JSONObject.toJSON(list), 20, TimeUnit.MINUTES);

        return list;
    }

    @Override
    public List<Integer> getLotteryIdsByUserId(Integer userId) {
        LambdaQueryWrapper<LotteryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LotteryRecord::getUserId, userId);
        return lotteryRecordMapper.selectList(queryWrapper).stream()
                .map(LotteryRecord::getLotteryId)
                .collect(Collectors.toList());
    }


    // 可以使用行级锁查询符合条件的记录,或者redis分布式锁
    private synchronized Lottery getRandomLotteryByGenderAndSchoolIdAndDrawCount(int gender, int schoolId, int drawCount, List<Integer> lotteryIds, Integer userId) {

        LambdaQueryWrapper<Lottery> queryWrapper = new LambdaQueryWrapper<Lottery>()
                .eq(Lottery::getGender, gender)
                .eq(Lottery::getSchoolId, schoolId)
                .eq(Lottery::getDrawCount, drawCount)
                .ne(Lottery::getUserId, userId)
                .notIn(Lottery::getId, lotteryIds);


        List<Lottery> lotteryList = lotteryMapper.selectList(queryWrapper);

        if (lotteryList.isEmpty()) {
            // 如果没有满足条件的记录，则递归调用自身，将drawCount加1
            //  注意点，可以设置最多比如每个纸条匹配5次，也可以还有防止空数据
            if (drawCount > 5) {
                throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
            }
            return getRandomLotteryByGenderAndSchoolIdAndDrawCount(gender, schoolId, drawCount + 1, lotteryIds, userId);
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
    private void checkStrategy(Integer schoolId,Integer userId) {
        // 将配置文件中的字符串转换为枚举值
        Strategy strategy = Strategy.valueOf(wallConfig.getStrategyString().toUpperCase());

        Integer luckyDraws = schoolMapper.selectById(schoolId).getNumberLuckyDraws();
        if (luckyDraws<1){
            throw new WallException(NOT_OPEN_LOVE_WALL);
        }

        // 根据策略进行检查
        switch (strategy) {
            case YEARLY:
                // 查询过去一年该用户是否已经抽过奖
                int yearlyCount = lotteryRecordMapper.countYearlyRecords(userId);
                if (yearlyCount >= luckyDraws) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
            case MONTHLY:
                // 查询过去一个月该用户是否已经抽过奖
                int monthlyCount = lotteryRecordMapper.countMonthlyRecords(userId);
                if (monthlyCount >= luckyDraws) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
            case DAILY:
                // 查询过去一天该用户是否已经抽过奖
                int dailyCount = lotteryRecordMapper.countDailyRecords(userId);
                if (dailyCount >= luckyDraws) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
            case ONCE:
                // 查询该用户是否已经抽过奖
                int totalCount = lotteryRecordMapper.countTotalRecords(userId);
                if (totalCount >= luckyDraws) {
                    throw new WallException(WITHDRAWAL_EXCEEDS_LIMIT);
                }
                break;
        }
    }

}
