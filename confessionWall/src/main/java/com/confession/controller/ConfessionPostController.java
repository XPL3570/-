package com.confession.controller;


import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.dto.ConfessionPostDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.request.AuditRequest;
import com.confession.request.ConfessionPostRequest;
import com.confession.request.ParameterIntTypeRequest;
import com.confession.service.AdminService;
import com.confession.service.ConfessionPostService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.confession.comm.ResultCodeEnum.LOGIN_ACL;


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
    private ConfessionPostService confessionPostService;


    @Resource
    private AdminService adminService;

    /**
     * 查看学校的投稿内容   这里要不要做一个查询表白墙表状态是否是可用的状态，不查询了，后面再说
     */
    @GetMapping("readConfessionWall")
    public Result readConfessionWall(@RequestParam Integer wallId, @ModelAttribute PageTool pageTool) {
        //如果每页记录数太大了，直接返回错误
        if (pageTool.getLimit() > 19) {
            return Result.fail();
        }
        List<ConfessionPostDTO> res = confessionPostService.confessionPostService(wallId, pageTool.getPage(), pageTool.getLimit());
        return Result.ok(res);
    }


    /**
     * 提交投稿，每个人限制每天投稿次数
     */
    @PostMapping("submit")
    public Result submitConfessionWall(@RequestBody @Validated ConfessionPostRequest confessionRequest) {
        int status = confessionPostService.userSubmitConfessionWall(confessionRequest);
        String message = (status == 1) ? "发布成功" : "等待管理员审核";
        return Result.build(200, message);
    }

    /**
     * 查看用户审核过的投稿记录
     */
    @GetMapping("/published")
    public Result<List<ConfessionPostDTO>> getPublishedPosts(@ModelAttribute PageTool pageTool) {
        Integer userId = JwtInterceptor.getUser().getId(); // 从 Thread 中获取用户ID
        List<ConfessionPostDTO> posts = confessionPostService.getPublishedPosts(userId, pageTool);
        return Result.ok(posts);
    }

    /**
     * 查看用户正在审核的投稿记录
     */
    @GetMapping("/pending")
    public Result<List<ConfessionPostDTO>> getPendingPosts(@ModelAttribute PageTool pageTool) {
        Integer userId = JwtInterceptor.getUser().getId(); // 从 Thread 中获取用户ID
        List<ConfessionPostDTO> posts = confessionPostService.getPendingPosts(userId, pageTool);
        return Result.ok(posts);
    }

    /**
     * 查询表白墙下要审核的记录
     */
    @GetMapping("userAdmin/pending")
    public Result getPosts(@RequestParam Integer wallId, @ModelAttribute PageTool pageTool) {
        Integer id = JwtInterceptor.getUser().getId();

        boolean admin = adminService.isAdmin(id, wallId);
        if (!admin) {
            throw new WallException(LOGIN_ACL);
        }
        return Result.ok(confessionPostService.getPendingPostsAdmin(wallId, pageTool));
    }

    /**
     * 审核请求  这个接口没有超级管理员特权
     */
    @PostMapping("userAdmin/submissionReview")
    public Result submissionReview(@RequestBody @Validated AuditRequest request) {
        Integer id = JwtInterceptor.getUser().getId();
        confessionPostService.submissionReview(id, request);
        return Result.ok();
    }

    /**
     * 超级管理员查看发布内容列表
     * 这里的sql后面应该可以优化，中间牵扯到两个外表，可以用小表驱动大表，还有的查询多余字段
     *
     * @param pageTool          分页参数
     * @param fuzzyQueryContent 模糊查询标题和文字内容
     * @param wallName          墙名字
     * @param userName          用户名
     * @param isAnonymous       是否匿名
     * @param isAdminPost       是否超级管理员发布的所有人可查看
     * @param postStatus        发布状态
     * @param reverseOrder      是否倒序   这里可能直接按照id倒序
     * @return
     */
    @GetMapping("admin/userList")
    public Result userList(@ModelAttribute PageTool pageTool,
                           @RequestParam(required = false) String fuzzyQueryContent,
                           @RequestParam(required = false) String wallName,
                           @RequestParam(required = false) String userName,
                           @RequestParam(required = false) Boolean isAnonymous,
                           @RequestParam(required = false) Boolean isAdminPost,
                           @RequestParam(required = false) Integer postStatus,
                           @RequestParam(required = false) Boolean reverseOrder) {
        PageResult result = confessionPostService.confessionWallContentQuery
                (pageTool, fuzzyQueryContent, wallName, userName, isAnonymous, isAdminPost, postStatus, reverseOrder);
        return Result.ok(result);
    }

    /**
     * 修改发布状态
     */
    @PostMapping("admin/modifyState")
    public Result modifyState(@RequestBody @Validated AuditRequest request) { //这里的请求参数表白墙id也用不到
        confessionPostService.modifyPublishingStatus(request);
        return Result.ok();
    }

    /**
     * 删除投稿内容
     */
    @PostMapping("admin/delete")
    public Result adminDelete(@RequestBody @Validated ParameterIntTypeRequest request) {
        confessionPostService.removeById(request.getRequestId());
        return Result.ok();
    }


}

