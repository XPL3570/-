package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageTool;
import com.confession.comm.SensitiveTextFilter;
import com.confession.config.WallConfig;
import com.confession.dto.CommentDTO;
import com.confession.dto.UserDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.CommentMapper;
import com.confession.mapper.UserMapper;
import com.confession.pojo.Comment;
import com.confession.pojo.User;
import com.confession.request.PostCommentRequest;
import com.confession.service.CommentService;
import com.confession.service.UserService;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.confession.comm.ResultCodeEnum.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserService userService;

    @Resource
    private SensitiveTextFilter sensitiveTextFilter;

    @Resource
    private WallConfig wallConfig;

    @Resource
    private UserMapper userMapper;


    @Override
    public Integer publishCommentReply(PostCommentRequest request, Integer userId) {

        //todo  将来可能会把这个评论的发表直接同步到缓存,用户信息会上缓存

        //调用方法对敏感字进行匹配，如果匹配到了，直接打回
        Boolean hasSensitiveWords = this.hasSensitiveWords(request.getCommentContent());
        if (hasSensitiveWords){
            throw new WallException(COMMENT_SENSITIVE_WORD_ALARM);
        }
        //判断用户是不是可以评论的
        User user = userMapper.selectById(userId);  //检查用户状态是否可以评论
        int userStatus=user.getStatus();
        if (userStatus==2||userStatus==3){
            throw new WallException(CANNOT_COMMENT);
        }
        int count = commentMapper.getCommentCountByUserIdAndDate(userId, LocalDate.now());

        if (count >= wallConfig.getUserDailyCommentLimit()) {  //判断是否超过每天评论限制
            throw new WallException(COMMENT_OVER_LIMIT);
        }
        Comment comment = new Comment();
        comment.setConfessionPostReviewId(request.getConfessionPostReviewId());
        comment.setParentCommentId(request.getParentCommentId());
        comment.setUserId(userId);
        comment.setCommentContent(request.getCommentContent());
        comment.setCommentTime(LocalDateTime.now());
        // 获取插入数据的 ID
        commentMapper.insert(comment);
        Integer commentId = comment.getId();
        return commentId;
    }


    @Override
    public List<CommentDTO> viewRecordsOnId(Integer contentId, boolean isMain) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getConfessionPostReviewId, contentId);
        List<Comment> list = commentMapper.selectList(wrapper);
        List<CommentDTO> comments = new ArrayList<>();
        for (Comment comment : list) {
            UserDTO userDTO = userService.getUserFromRedisOrDatabase(comment.getUserId());
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUserName(userDTO.getUsername());
            commentDTO.setAvatarURL(userDTO.getAvatarURL());
            if ((isMain && comment.getParentCommentId() == null) || (!isMain && comment.getParentCommentId() != null)) {
                comments.add(commentDTO);
            }
        }
        return comments;
    }

    @Override
    public List<CommentDTO> getRepliesToUserComments(Integer userId, PageTool pageTool) {
        Page<Comment> page = new Page<>(pageTool.getPage(), pageTool.getLimit());
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.inSql(Comment::getParentCommentId, "SELECT Id FROM comment WHERE userId = " + userId)
                .ge(Comment::getCommentTime, LocalDateTime.now().minusMonths(6))
                .ne(Comment::getUserId, userId)
                .orderByDesc(Comment::getCommentTime);
        List<Comment> list = commentMapper.selectPage(page, queryWrapper).getRecords();
        List<CommentDTO> comments = new ArrayList<>();
        for (Comment comment : list) {
            UserDTO userDTO = userService.getUserFromRedisOrDatabase(comment.getUserId());
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUserName(userDTO.getUsername());
            commentDTO.setAvatarURL(userDTO.getAvatarURL());
            comments.add(commentDTO);
        }
        return comments;
    }

    @Override
    public Boolean hasSensitiveWords(String text) {
        AhoCorasickDoubleArrayTrie<String> trie = sensitiveTextFilter.getTrie(); // 获取敏感词库的 Trie 实例
        Collection<AhoCorasickDoubleArrayTrie.Hit<String>> hits = trie.parseText(text);
        if (hits.isEmpty()) {
            return false; // 没有敏感词匹配，直接返回 false
        }
        if (hits.size()>0){
            return true;
        }
        return false;
    }


}
