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
import com.confession.mapper.SchoolMapper;
import com.confession.pojo.Lottery;
import com.confession.request.LotteryRequest;
import com.confession.service.LotteryService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.confession.comm.RedisConstant.USER_POSTED_NODE;
import static com.confession.comm.ResultCodeEnum.NOT_OPEN_LOVE_WALL;
import static com.confession.comm.ResultCodeEnum.SUBMISSION_EXCEEDS_LIMIT;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年09月16日
 */
@Service
public class LotteryServiceImpl extends ServiceImpl<LotteryMapper, Lottery> implements LotteryService {

    @Resource
    private LotteryMapper lotteryMapper;

    @Resource
    private WallConfig wallConfig;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SchoolMapper schoolMapper;

    @Override
    public boolean insert(LotteryRequest request, Integer userId) {
        // 检查策略
        checkStrategy(request.getSchoolId(),userId);
        // 如果检查通过，执行写入操作
        Lottery lottery = new Lottery();
        lottery.setUserId(userId);
        lottery.setSchoolId(request.getSchoolId());
        lottery.setContactInfo(request.getContactInfo());
        lottery.setImageUrl(request.getImageUrl());
        lottery.setGender(request.getGender());
        lottery.setIntroduction(request.getIntroduction());
        boolean saveOk = save(lottery);
        redisTemplate.delete(USER_POSTED_NODE + userId);
        return saveOk;
    }

    @Override
    public List<Lottery> postedNote(Integer userId) {
        JSON json = (JSON) redisTemplate.opsForValue().get(USER_POSTED_NODE + userId);
        if (json != null) {
            return json.toJavaObject(List.class);
        }
        LambdaQueryWrapper<Lottery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Lottery::getUserId, userId);
        wrapper.orderByDesc(Lottery::getId);
        Page<Lottery> page = new Page<>(1, 20);
        List<Lottery> records = lotteryMapper.selectPage(page, wrapper).getRecords();
        redisTemplate.opsForValue().set(USER_POSTED_NODE + userId, JSONObject.toJSON(records), 20, TimeUnit.MINUTES);
        return records;
    }

    private void checkStrategy(Integer schoolId,Integer userId) {
        // 将配置文件中的字符串转换为枚举值
        //这里可以也许可以用反射优化，这里先不管了
        Strategy strategy = Strategy.valueOf(wallConfig.getStrategyString().toUpperCase());

        Integer luckyDraws = schoolMapper.selectById(schoolId).getNumberPaperInputs();
        if (luckyDraws<1){
            throw new WallException(NOT_OPEN_LOVE_WALL);
        }
        // 根据策略进行检查
        switch (strategy) {
            case YEARLY:
                // 查询过去一年该用户是否已经创建过记录
                int yearlyCount = lotteryMapper.countYearlyRecords(userId);
                if (yearlyCount >= luckyDraws) {
                    throw new WallException(SUBMISSION_EXCEEDS_LIMIT);
                }
                break;
            case MONTHLY:
                // 查询过去一个月该用户是否已经创建过记录
                int monthlyCount = lotteryMapper.countMonthlyRecords(userId);
                if (monthlyCount >= luckyDraws) {
                    throw new WallException(SUBMISSION_EXCEEDS_LIMIT);
                }
                break;
            case DAILY:
                // 查询过去一天该用户是否已经创建过记录
                int dailyCount = lotteryMapper.countDailyRecords(userId);
                if (dailyCount >= luckyDraws) {
                    throw new WallException(SUBMISSION_EXCEEDS_LIMIT);
                }
                break;
            case ONCE:
                // 查询该用户是否已经创建过记录
                int totalCount = lotteryMapper.countTotalRecords(userId);
                if (totalCount >=luckyDraws) {
                    throw new WallException(SUBMISSION_EXCEEDS_LIMIT);
                }
                break;
        }
    }


}
