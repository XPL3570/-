package com.confession.controller;


import com.confession.comm.PageTool;
import com.confession.comm.Result;
import com.confession.dto.CommentDTO;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.Comment;
import com.confession.request.PostCommentRequest;
import com.confession.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
        if (commentId!=null) {
            return Result.ok(commentId);
        } else {
            return Result.fail();
        }
    }

    /**
     * 查询用户评论回复 -半年内
     */
    @GetMapping("repliesWithComments")
    public Result repliesWithComments(@ModelAttribute PageTool pageTool){
        Integer userId = JwtInterceptor.getUser().getId();
        List<CommentDTO> comments = commentService.getRepliesToUserComments(userId,pageTool);

        return Result.ok(comments);
    }


}

