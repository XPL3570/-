package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.dto.CommentDTO;
import com.confession.pojo.Comment;
import com.confession.request.PostCommentRequest;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
public interface CommentService extends IService<Comment> {

    /**
     * 发布评论
     * @param request 评论参数
     * @param userId  评论人id
     * @return
     */
    Integer  publishCommentReply(PostCommentRequest request,Integer userId);




    /**
     * 查询表白id下的评论
     * @param contentId 要查询内容id的评论
     * @param isMain 是否是主评论
     * @return
     */
    List<CommentDTO> viewRecordsOnId(Integer contentId,boolean isMain);




}
