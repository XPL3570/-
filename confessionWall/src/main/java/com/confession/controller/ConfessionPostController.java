package com.confession.controller;


import com.confession.comm.PageTool;
import com.confession.comm.RedisConstant;
import com.confession.comm.Result;
import com.confession.config.WallConfig;
import com.confession.dto.ConfessionPostDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.pojo.Confessionpost;
import com.confession.request.ConfessionPostRequest;
import com.confession.request.ReadConfessionRequest;
import com.confession.service.ConfessionpostService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.confession.comm.ResultCodeEnum.CONTRIBUTE_OVER_LIMIT;
import static com.confession.comm.ResultCodeEnum.DATA_ERROR;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("/api/confessionPost")
public class ConfessionPostController {


    @Resource
    private ConfessionpostService confessionPostService;

    @Resource
    private ConfessionwallMapper confessionwallMapper;

    @Resource
    private WallConfig wallConfig;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 查看学校的投稿内容    todo 可以在一开始就把数据加载到缓存
     */
    @PostMapping("readConfessionWall")
    public Result readConfessionWall(@RequestBody ReadConfessionRequest request){
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        // 查询Redis中是否存在满足条件的数据
        long startTimestamp = request.getRecordAfterTime(); // 前端传递的时间戳参数
        long endTimestamp = Instant.now().getEpochSecond();
        Integer pageSize= request.getPageSize();
        String key=RedisConstant.CONFESSION_PREFIX+request.getWallId();
        Set<Object> pageData = zSetOperations.rangeByScore(key, startTimestamp, endTimestamp);

        System.out.println(pageData);

        if (pageData != null && !pageData.isEmpty()) {
            List<Object> result;

            if (pageData.size() >= pageSize) {
                // 如果Redis中有足够数量的数据满足条件，直接返回给客户端
                result = new ArrayList<>(pageData).subList(0, pageSize);
            } else {
                // 否则需要从数据库查询剩余的数据
                int remainingCount = pageSize - pageData.size();

                // 根据最后一条记录的时间参数，从数据库查询剩余的数据
                Double lastTimestamp = zSetOperations.score(key, pageData.toArray()[pageData.size() - 1]);
                List<ConfessionPostDTO> remainingData =
                        confessionPostService.getPostsAfterTimestamp(request.getWallId(), lastTimestamp.longValue(), remainingCount);

                // 合并两部分数据，并返回给客户端
                List<Object> mergedData = new ArrayList<>(pageData);
                mergedData.addAll(remainingData);
                result = mergedData.subList(0, pageSize);
            }
            return Result.ok(result);
        } else {
            // 如果Redis中没有满足条件的数据，则直接从数据库查询
            List<ConfessionPostDTO> postData = confessionPostService.getPostsAfterTimestamp(request.getWallId(), startTimestamp, pageSize);
            return Result.ok(postData);
        }

    }






    /**
     *   提交投稿，每个人限制每天投稿次数  todo 这里没有牵扯到缓存
     * @param confessionRequest
     * @return
     */
    @PostMapping("submit")
    public Result submitConfessionWall(@RequestBody ConfessionPostRequest confessionRequest) {
        Integer userId = JwtInterceptor.getUser().getId();
        Integer schoolId = JwtInterceptor.getUser().getSchoolId();

        //判断该用户每天的投稿有没有超过限制
        int count = confessionPostService.getPostCountByUserIdAndDate(userId, LocalDate.now());
        if (count >= wallConfig.getUserDailyPostLimit()) {
            throw new WallException(CONTRIBUTE_OVER_LIMIT);
        }

        //查询用户绑定的学校id是否和墙id是否对应，这里只是为了安全，反正这个接口调用次数有限
        Integer wallInSchool = confessionwallMapper.isWallInSchool(schoolId, confessionRequest.getWallId());
        if (wallInSchool == null) {
            throw new WallException(DATA_ERROR);
        }

        boolean hasImage = (confessionRequest.getImageURL() != null && !confessionRequest.getImageURL().isEmpty());
        int status = 0;

        if (!hasImage) {
            boolean filterResult = confessionPostService.filterContent(confessionRequest);
            if (filterResult) {
                status = 1;
            }
        }
        Confessionpost confessionPost = new Confessionpost();
        if (status == 1) {
            confessionPost.setPublishTime(LocalDateTime.now());
        }

        confessionPost.setUserId(userId);
        confessionPost.setWallId(confessionRequest.getWallId());
        confessionPost.setImageURL(confessionRequest.getImageURL());
        confessionPost.setTitle(confessionRequest.getTitle());
        confessionPost.setTextContent(confessionRequest.getTextContent());
        confessionPost.setIsAnonymous(confessionRequest.getIsAnonymous());
        confessionPost.setPostStatus(status);

        confessionPostService.save(confessionPost);

        if (status == 1) {
            String key = RedisConstant.CONFESSION_PREFIX+confessionPost.getWallId();
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            zSetOperations.add(key, confessionPost, confessionPost.getPublishTime().toEpochSecond(ZoneOffset.UTC));
            redisTemplate.expire(key, 3, TimeUnit.DAYS); //这里也可以是设置成可以配置，天数
        }

        String message = (status == 1) ? "发布成功" : "等待管理员审核";
        return Result.build(200, message);
    }

    /**
     * 查看用户审核过的投稿记录
     */
    @GetMapping("/published")
    public Result<List<ConfessionPostDTO>> getPublishedPosts(@ModelAttribute PageTool pageTool) {
        Integer userId = JwtInterceptor.getUser().getId(); // 从 Thread 中获取用户ID
        List<ConfessionPostDTO> posts = confessionPostService.getPublishedPosts(userId,pageTool);
        return Result.ok(posts);
    }

    /**
     * 查看用户正在审核的投稿记录
     */
    @GetMapping("/pending")
    public Result<List<ConfessionPostDTO>> getPendingPosts(@ModelAttribute PageTool pageTool) {
        Integer userId = JwtInterceptor.getUser().getId(); // 从 Thread 中获取用户ID
        List<ConfessionPostDTO> posts = confessionPostService.getPendingPosts(userId,pageTool);
        return Result.ok(posts);
    }

}

