package com.confession.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.RedisConstant;
import com.confession.comm.Result;
import com.confession.config.WallConfig;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.Admin;
import com.confession.pojo.Confessionpost;
import com.confession.request.AdminLoginRequest;
import com.confession.request.ConfessionPostRequest;
import com.confession.service.AdminService;
import com.confession.service.ConfessionPostService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.confession.comm.ResultCodeEnum.CONTRIBUTE_OVER_LIMIT;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private ConfessionPostService confessionPostService;


    @Resource
    private WallConfig wallConfig;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 超级管理员登录
     */
    @PostMapping("login")
    public Result adminLogin(@RequestBody @Validated AdminLoginRequest adminLoginRequest){
        Map res = adminService.login(adminLoginRequest);
        return Result.ok(res);
    }

    @GetMapping("list")
    public Result list(@ModelAttribute PageTool pageTool){
        Page<Admin> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<Admin> admins = adminService.page(page).getRecords();
        admins.stream().forEach(admin -> admin.setPassword(""));
        PageResult result = new PageResult(admins, page.getTotal(), admins.size());
        return Result.ok(result);
    }



    /**
     * 发布投稿，直接通过  这里指定了某个学校  普通管理员可用  但是不能调用
     *
     * @param confessionRequest
     * @return
     */
    @PostMapping("/submit")
    public Result submitConfessionPostAsAdmin(@RequestBody @Validated ConfessionPostRequest confessionRequest) {
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

        Confessionpost confessionPost = createConfessionPost(confessionRequest, userId, false);

        confessionPostService.save(confessionPost);

        saveToRedis(confessionPost);

        return Result.ok();
    }

    @PostMapping("/allSubmitPost")
    public Result submitConfessionAll(@RequestBody @Validated ConfessionPostRequest confessionRequest) {
        Integer adminId = JwtInterceptor.getUser().getId(); //这个id是管理员表的id
        //加一个限制，限制管理员每天只能发布两条全部类型的投稿,这里写死了
        int count= confessionPostService.getAdminPostCount();
        System.out.println("count="+count);
        if (count>=2){
            return Result.build(224,"失败，每天只能发布两条所有人都能看到的投稿哦");
        }
        Confessionpost confessionPost = createConfessionPost(confessionRequest, adminId, true);
        confessionPostService.save(confessionPost);

        saveToRedis(confessionPost);

        return Result.ok();
    }

    private Confessionpost createConfessionPost(ConfessionPostRequest confessionRequest, Integer userId, boolean isAdminPost) {
        Confessionpost confessionPost = new Confessionpost();
        confessionPost.setUserId(userId);
        confessionPost.setIsAnonymous(confessionRequest.getIsAnonymous());
        confessionPost.setWallId(confessionRequest.getWallId());
        confessionPost.setImageURL(confessionRequest.getImageURL());
        confessionPost.setTitle(confessionRequest.getTitle());
        confessionPost.setTextContent(confessionRequest.getTextContent());
        confessionPost.setPostStatus(1); // 直接设置为审核通过
        confessionPost.setIsAdminPost(isAdminPost); //设置成管理员发布，所有表白墙都能看到
        confessionPost.setPublishTime(LocalDateTime.now()); //设置成现在发布
        return confessionPost;
    }

    private void saveToRedis(Confessionpost confessionPost) {
        //保存到redis
        String key = RedisConstant.CONFESSION_PREFIX + confessionPost.getWallId();
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, confessionPost, confessionPost.getPublishTime().toEpochSecond(ZoneOffset.UTC));
        redisTemplate.expire(key, 3, TimeUnit.DAYS);
    }









}

