package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageResult;
import com.confession.comm.PageTool;
import com.confession.dto.ConfessionPostDTO;
import com.confession.pojo.Confessionpost;
import com.confession.request.AuditRequest;
import com.confession.request.ConfessionPostRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
public interface ConfessionpostService extends IService<Confessionpost> {

    /**
     * 查询用户当天的发布数量
     */
    int getPostCountByUserIdAndDate(Integer userId, LocalDate date);

    /**
     * 过滤表白墙发布内容
     * @param confessionRequest 表白墙发布请求，里面会对敏感字进行替换
     * @return true表示内容合法，false表示内容不合法
     */
    boolean filterContent(ConfessionPostRequest confessionRequest);

    /**
     * 查询审核过状态的用户投稿记录
     * @return
     */
    List<ConfessionPostDTO> getPublishedPosts(Integer userId, PageTool pageTool);

    /**
     * 查询正在审核的用户投稿记录
     * @return
     */
    List<ConfessionPostDTO> getPendingPosts(Integer userId, PageTool pageTool);

    /**
     * 查询内容以及评论
     * @param wallId  墙id
     * @param timestamp  查询这个时间戳之后的数据
     * @param count  要查询几条
     * @return
     */
    List<ConfessionPostDTO> getPostsAfterTimestamp(Integer wallId, Long timestamp, Integer count);


    /**
     * 获取墙内要审核的投稿
     * @param wallId
     */
    List<ConfessionPostDTO> getPendingPostsAdmin(Integer wallId,PageTool pageTool);

    /**
     * 修改投稿的状态
     * @param userId  用户id
     * @param request 投稿id和要修改的状态
     */
    void submissionReview(Integer userId, AuditRequest request);

    /**
     * 获取超级管理员发布所以人都能看到的表白投稿数量
     * @return
     */
    Integer getAdminPostCount();

    /**
     *  超级管理员查询发布内容   很多sql可以优化，这里先不管，反正管理员用的
     */
    PageResult confessionWallContentQuery(PageTool pageTool, String fuzzyQueryContent, String wallName, String userName, Boolean isAnonymous,Boolean isAdminPost, Integer postStatus, Boolean reverseOrder);

    /**
     * 管理员修改发布状态
     */
    void modifyPublishingStatus(AuditRequest request);
}
