package com.confession.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.pojo.Confessionwall;
import com.confession.pojo.School;
import com.confession.request.RegistryWhiteWallRequest;
import com.confession.service.ConfessionwallService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 表白墙
 *
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("/api/confession")
public class ConfessionWallController {
    @Resource
    private ConfessionwallService confessionwallService;

    /**
     * 注册 不同审核，反正用户是通过学校名字来绑定的   展示  注意这里目前学校和墙是一对一的
     *
     * 注意这里也是不要登录的 看着优化
     */
    @PostMapping("register")
    public Result register(@RequestBody @Validated RegistryWhiteWallRequest registryWhiteWallRequest){
        confessionwallService.register(registryWhiteWallRequest);
        return Result.ok();
    }

    /**
     * 查看表白墙列表
     */
    @GetMapping("admin/list")
    public Result list(@ModelAttribute PageTool pageTool){
        Page<Confessionwall> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        List<Confessionwall> list = confessionwallService.page(page).getRecords();
        return Result.ok(list);
    }







}

