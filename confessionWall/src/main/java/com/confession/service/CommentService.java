package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageTool;
import com.confession.dto.CommentDTO;
import com.confession.pojo.Comment;
import com.confession.request.PostCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
public interface CommentService extends IService<Comment> {

    /**
     * 发布评论
     * @param request 评论参数
     * @param userId  评论人id
     * @return
     */
    Integer publishCommentReply(PostCommentRequest request,Integer userId);


    /**
     * 查询表白id下的评论
     * @param contentId 要查询内容id的评论
     * @param isMain 是否是主评论
     * @return
     */
    List<CommentDTO> viewRecordsOnId(Integer contentId,boolean isMain);

    /**  查询用户半年内的发布的评论的回复
     *
     * @param userId
     * @param pageTool
     * @return
     */
    List<CommentDTO> getRepliesToUserComments(Integer userId, PageTool pageTool);

    /**
     * 是否有敏感字
     */
    Boolean hasSensitiveWords(String text);

    /**
     * 用户删除自己的评论，加一个redis缓存，比如允许用户每12小时删除一条评论或者投稿
     */
    void deleteComment(Integer commentId);


    /**
     * 查询该时间之后的评论回复数量
     */
    int numberUnreadCommentsByUsers(Integer userId, LocalDateTime dateTime);


}
