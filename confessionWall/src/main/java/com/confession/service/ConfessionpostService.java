package com.confession.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.confession.comm.PageTool;
import com.confession.dto.ConfessionPostDTO;
import com.confession.pojo.Confessionpost;
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
     * @param confessionRequest 表白墙发布请求
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



}
