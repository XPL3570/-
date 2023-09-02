package com.confession.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.comm.Result;
import com.confession.config.WallConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionwallMapper;
import com.confession.pojo.Confessionpost;
import com.confession.request.ConfessionPostRequest;
import com.confession.service.AdminService;
import com.confession.service.ConfessionpostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.time.LocalDate;

import static com.confession.comm.ResultCodeEnum.CONTRIBUTE_OVER_LIMIT;
import static com.confession.comm.ResultCodeEnum.DATA_ERROR;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private ConfessionpostService confessionPostService;


    @Resource
    private WallConfig wallConfig;

    /**
     *  发布投稿，直接通过
     * @param confessionRequest
     * @return
     */
    @PostMapping("/submit")
    public Result submitConfessionPostAsAdmin(@RequestBody ConfessionPostRequest confessionRequest) {
        Integer userId = JwtInterceptor.getUser().getId();

        // 校验管理员身份
        boolean isAdmin = adminService.isAdmin(userId, confessionRequest.getWallId());
        if (!isAdmin) {
            return Result.fail("您不是该表白墙的管理员");
        }

        //判断该管理员每天的投稿有没有超过限制
        int count = confessionPostService.getPostCountByUserIdAndDate(userId, LocalDate.now());
        if (count >= wallConfig.getAdminDailyPostLimit()) {
            throw new WallException(CONTRIBUTE_OVER_LIMIT);
        }
        Confessionpost confessionPost = new Confessionpost();
        confessionPost.setUserId(userId);
        confessionPost.setWallId(confessionRequest.getWallId());
        confessionPost.setImageURL(confessionRequest.getImageURL());
        confessionPost.setTitle(confessionRequest.getTitle());
        confessionPost.setTextContent(confessionRequest.getTextContent());
        confessionPost.setPostStatus(1); // 直接设置为审核通过

        confessionPostService.save(confessionPost);

        return Result.ok();
    }

}

