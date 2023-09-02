package com.confession.controller;


import com.confession.comm.Result;
import com.confession.config.WallConfig;
import com.confession.dto.ConfessionPostDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.pojo.Confessionpost;
import com.confession.request.ConfessionPostRequest;
import com.confession.service.ConfessionpostService;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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


    /**
     * 查看用户审核过的投稿记录
     */
    @GetMapping("/published")
    public Result<List<ConfessionPostDTO>> getPublishedPosts() {
        Integer userId = JwtInterceptor.getUser().getId(); // 从 Thread 中获取用户ID
        List<ConfessionPostDTO> posts = confessionPostService.getPublishedPosts(userId);
        return Result.ok(posts);
    }

    /**
     * 查看用户正在审核的投稿记录
     * @return
     */
    @GetMapping("/pending")
    public Result<List<ConfessionPostDTO>> getPendingPosts() {
        Integer userId = JwtInterceptor.getUser().getId(); // 从 Thread 中获取用户ID
        List<ConfessionPostDTO> posts = confessionPostService.getPendingPosts(userId);
        return Result.ok(posts);
    }





    /**
     *   提交投稿，每个人限制每天投稿次数
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
        confessionPost.setPostStatus(status);

        confessionPostService.save(confessionPost);

        String message = (status == 1) ? "发布成功" : "等待管理员审核";
        return Result.build(200, message);
    }


}

