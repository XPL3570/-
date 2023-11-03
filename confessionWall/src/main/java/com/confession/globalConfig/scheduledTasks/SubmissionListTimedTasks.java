package com.confession.globalConfig.scheduledTasks;

import com.confession.mapper.ConfessionwallMapper;
import com.confession.pojo.ConfessionWall;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static com.confession.comm.RedisConstant.SCHOOL_WALL_MAIN_LIST_MOD_LOCK;
import static com.confession.comm.RedisConstant.POST_SUBMISSION_RECORD;

/**
 * 投稿列表定时任务 每天凌晨三点去定时的更新一次学校列表 ，因为每个学校或者每个表白墙的key是基本不会过期的，
 * 不清理怕列表过长，这里不删除列表下面的投稿数据了，后面可能再优化
 */
@Component
public class SubmissionListTimedTasks {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ConfessionwallMapper confessionwallMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void organizeListAllConfessionWalls(){
        List<ConfessionWall> list = confessionwallMapper.selectList(null);
        for (ConfessionWall confessionwall : list) {
            this.cleanUpWallList(confessionwall.getId());
        }
    }

    private void cleanUpWallList(Integer wallId) {
        RLock lock = redissonClient.getLock(SCHOOL_WALL_MAIN_LIST_MOD_LOCK + wallId);
        lock.lock();
        try {
            // 获取有序集合中的最后157个元素
            Set<String> last157Members = redisTemplate.opsForZSet().reverseRange(POST_SUBMISSION_RECORD + wallId, 0, 156);
            if (last157Members.size()>=157){
                // 删除原始有序集合中的所有元素
                redisTemplate.opsForZSet().removeRange(POST_SUBMISSION_RECORD + wallId, 0, -1);

                // 将最后157个元素重新添加到原始有序集合中
                for (String member : last157Members) {
                    redisTemplate.opsForZSet().add(POST_SUBMISSION_RECORD + wallId, member, 0);
                }
            }
        } finally {
            lock.unlock();
        }
    }



}
