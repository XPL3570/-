package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageTool;
import com.confession.comm.SensitiveTextFilter;
import com.confession.dto.ConfessionPostDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.mapper.ConfessionpostMapper;
import com.confession.pojo.Confessionpost;
import com.confession.request.AuditRequest;
import com.confession.request.ConfessionPostRequest;
import com.confession.service.AdminService;
import com.confession.service.CommentService;
import com.confession.service.ConfessionpostService;
import com.confession.service.UserService;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.confession.comm.ResultCodeEnum.LOGIN_ACL;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class ConfessionpostServiceImpl extends ServiceImpl<ConfessionpostMapper, Confessionpost> implements ConfessionpostService {

    @Resource
    private ConfessionpostMapper confessionpostMapper;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private AdminService adminService;

    @Resource
    private SensitiveTextFilter sensitiveTextFilter;

    @Override
    public int getPostCountByUserIdAndDate(Integer userId, LocalDate date) {
        return confessionpostMapper.getPostCountByUserIdAndDate(userId, date);
    }

    /**
     * 合并敏感词匹配和替换：当前代码中使用两个循环分别对标题和文本内容进行敏感词匹配和替换，可以将它们合并为一个循环来避免重复遍历。
     *
     * 使用 StringBuilder 的 setCharAt 方法替换字符：当前代码中使用 replace 方法替换字符，每次替换都会创建一个新的 StringBuilder 对象，存在额外的开销。可以使用 setCharAt 方法直接修改字符。
     *
     * 避免不必要的循环：如果没有敏感词匹配，可以直接返回 false，无需进行后续处理。
     *
     * @param request 表白墙发布请求，里面会对敏感字进行替换
     * @return
     */
    @Override
    public boolean filterContent(ConfessionPostRequest request) {
        AhoCorasickDoubleArrayTrie<String> trie = sensitiveTextFilter.getTrie(); // 获取敏感词库的 Trie 实例
        String title = request.getTitle();
        String textContent = request.getTextContent();

        StringBuilder filteredTitleBuilder = new StringBuilder(title);
        StringBuilder filteredTextContentBuilder = new StringBuilder(textContent);

        Collection<AhoCorasickDoubleArrayTrie.Hit<String>> hits = trie.parseText(title + " " + textContent); // 合并标题和文本内容进行敏感词匹配

        if (hits.isEmpty()) {
            return false; // 没有敏感词匹配，直接返回 false
        }

        Boolean hasSensitiveWords = false; // 是否存在敏感词,默认是不存在

        if (hits.size()>0){
            hasSensitiveWords = true;
        }

        for (AhoCorasickDoubleArrayTrie.Hit<String> hit : hits) {
            int start = hit.begin; // 敏感词在输入文本中的起始位置
            int end = hit.end; // 敏感词在输入文本中的结束位置
            StringBuilder targetBuilder = (start < title.length()) ? filteredTitleBuilder : filteredTextContentBuilder;
            int targetStart = (start < title.length()) ? start : (start - title.length() - 1);
            int targetEnd = (end <= title.length()) ? end : (end - title.length() - 1);

            for (int i = targetStart; i < targetEnd; i++) {
                targetBuilder.setCharAt(i, '*');
            }
        }

        request.setTitle(filteredTitleBuilder.toString());
        request.setTextContent(filteredTextContentBuilder.toString());

        return hasSensitiveWords;
    }



    @Override
    public List<ConfessionPostDTO> getPublishedPosts(Integer userId, PageTool pageTool) {
        return getPostsByStatus(pageTool, userId, 1);
    }

    @Override
    public List<ConfessionPostDTO> getPendingPosts(Integer userId, PageTool pageTool) {
        return getPostsByStatus(pageTool, userId, 0);
    }


    @Override
    public List<ConfessionPostDTO> getPostsAfterTimestamp(Integer wallId, Long timestamp, Integer count) {
//        LambdaQueryWrapper<Confessionpost> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Confessionpost::getWallId, wallId)
////                .gt(Confessionpost::getPublishTime, LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC))
//                .eq(Confessionpost::getPostStatus, 1)
////                .orderByAsc(Confessionpost::getPublishTime)
//                .orderByAsc(Confessionpost::getId)  //todo 标注，是要按照时间来倒序查询的，为了测试
//                .last("LIMIT " + count);


        //todo 用下面的代码可以看到超级管理员直接发布的
        LambdaQueryWrapper<Confessionpost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Confessionpost::getWallId, wallId)
                .eq(Confessionpost::getPostStatus, 1)
                .or()
                .eq(Confessionpost::getIsAdminPost, true)
                .orderByDesc(Confessionpost::getCreateTime);


        List<Confessionpost> list = confessionpostMapper.selectList(queryWrapper);
        return list.stream().map(this::convertToDTOAll).collect(Collectors.toList());
    }

    @Override
    public List<ConfessionPostDTO> getPendingPostsAdmin(Integer wallId,PageTool pageTool) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Confessionpost::getPostStatus, 0);
        wrapper.eq(Confessionpost::getWallId,wallId);
        // 设置分页信息
        Page<Confessionpost> page = new Page<>(pageTool.getPage(), pageTool.getLimit());

        IPage<Confessionpost> iPage = confessionpostMapper.selectPage(page, wrapper);

        return iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void submissionReview(Integer userId, AuditRequest request) {
        boolean admin = adminService.isAdmin(userId, request.getWallId());
        if (!admin){
            throw new WallException(LOGIN_ACL);
        }
        LambdaUpdateWrapper<Confessionpost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Confessionpost::getId, request.getId())
                .set(Confessionpost::getPostStatus, request.getPostStatus())
                .set(Confessionpost::getPublishTime, LocalDateTime.now());
        int update = confessionpostMapper.update(null, updateWrapper);
        System.out.println(update);

    }

    @Override
    public Integer getAdminPostCount() {
        int adminPostCount = confessionpostMapper.getAdminPostCount(LocalDate.now());
        return adminPostCount;
    }


    //这里是普通用户使用的接口，加上了必须是用户发布的那个字段,获取用
    private List<ConfessionPostDTO> getPostsByStatus(PageTool pageTool, Integer userId, Integer postStatus) {
        LambdaQueryWrapper<Confessionpost> wrapper = new LambdaQueryWrapper<>();
        if (postStatus == 0) {
            wrapper.in(Confessionpost::getPostStatus, 0);
        } else {
            wrapper.notIn(Confessionpost::getPostStatus, 0);
        }
        wrapper.eq(Confessionpost::getUserId, userId);
        wrapper.eq(Confessionpost::getIsAdminPost,false);
        wrapper.orderByDesc(Confessionpost::getId); // 添加倒序排序条件
        // 设置分页信息
        Page<Confessionpost> page = new Page<>(pageTool.getPage(), pageTool.getLimit());

        IPage<Confessionpost> iPage = confessionpostMapper.selectPage(page, wrapper);

        return iPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //处理投稿数据，不要评论
    private ConfessionPostDTO convertToDTO(Confessionpost post) {
        ConfessionPostDTO dto = new ConfessionPostDTO();
        dto.setId(post.getId());
        dto.setWallId(post.getWallId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setTextContent(post.getTextContent());
        dto.setImageURL(Arrays.asList(post.getImageURL().split(";")));
        dto.setCreateTime(post.getCreateTime());
        dto.setPublishTime(post.getPublishTime());
        dto.setIsAnonymous(post.getIsAnonymous());
        dto.setPostStatus(post.getPostStatus());  //发布状态
        return dto;
    }

    //处理投稿数据，所有
    private ConfessionPostDTO convertToDTOAll(Confessionpost post) {
        ConfessionPostDTO dto = this.convertToDTO(post);
        if (!post.getIsAnonymous()) {
            dto.setUserInfo(userService.getUserFromRedisOrDatabase(post.getUserId()));
        }
        dto.setMainComments(commentService.viewRecordsOnId(post.getId(), true));
        dto.setSubComments(commentService.viewRecordsOnId(post.getId(), false));
        return dto;
    }
}
