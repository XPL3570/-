package com.confession.controller;


import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.dto.CommentDTO;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.request.PostCommentRequest;
import com.confession.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;


    /**
     * 查看记录下的评论
     */
    @PostMapping("viewComments")
    public Result viewComments(@ModelAttribute Integer contentId) {
//        List<CommentDTO> comments = commentService.viewRecordsOnId(contentId);
        return Result.ok(null);
    }

    /**
     * 发布评论
     *
     * @param request
     * @return
     */
    @PostMapping("publishReply")
    public Result publishReply(@RequestBody @Validated PostCommentRequest request) {
        Integer id = JwtInterceptor.getUser().getId();
        Integer commentId = commentService.publishCommentReply(request, id);
        if (commentId != null) {
            return Result.ok(commentId);
        } else {
            return Result.fail();
        }
    }

    /**
     * 查询用户评论回复 -半年内,投稿发布的回复也要
     */
    @GetMapping("repliesWithComments")
    public Result repliesWithComments(@Validated  @ModelAttribute PageTool pageTool) {
        Integer userId = JwtInterceptor.getUser().getId();
        List<CommentDTO> comments = commentService.getRepliesToUserComments(userId, pageTool);
        return Result.ok(comments);
    }

    /**
     * 给定时间，查询该时间之后的评论回复数，就是获取评论回复未读数量
     */
    @GetMapping("numberUnreadComments")
    public Result numberUnreadComments(@RequestParam("timestamp") Long timestamp){
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        if (dateTime.isBefore(sixMonthsAgo)) {
            return Result.ok(0);
        }
        Integer userId = JwtInterceptor.getUser().getId();
        return Result.ok(commentService.numberUnreadCommentsByUsers(userId,dateTime));
    }



}

