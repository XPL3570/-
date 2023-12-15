package com.confession.controller;


import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.request.ModifyWallRequest;
import com.confession.request.RegistryWhiteWallRequest;
import com.confession.service.ConfessionWallService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 表白墙控制器
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("/api/confession")
public class ConfessionWallController {
    @Resource
    private ConfessionWallService confessionwallService;

    /**
     * 注册 不同审核，反正用户是通过学校名字来绑定的   展示  注意这里目前学校和墙是一对一的
     * <p>
     * 注意这里也是不要登录的 看着优化
     */
    @PostMapping("register")
    public Result register(@RequestBody @Validated RegistryWhiteWallRequest registryWhiteWallRequest) {
        confessionwallService.register(registryWhiteWallRequest);
        return Result.ok();
    }

    /**
     * 查看表白墙列表
     */
    @GetMapping("admin/list")
    public Result list(@ModelAttribute @Validated PageTool pageTool) {
        PageResult result = confessionwallService.wallList(pageTool);
        return Result.ok(result);
    }

    /**
     * 修改表白墙信息
     */
    @PostMapping("admin/modifyWall")
    public Result modifyWall(@RequestBody @Validated ModifyWallRequest request){
        confessionwallService.modifyWall(request);
        return Result.ok();
    }


}

