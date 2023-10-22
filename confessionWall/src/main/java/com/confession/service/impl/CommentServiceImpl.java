package com.confession.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.comm.PageTool;
import com.confession.comm.RedisConstant;
import com.confession.comm.SensitiveTextFilter;
import com.confession.config.WallConfig;
import com.confession.dto.CommentDTO;
import com.confession.dto.ConfessionPostDTO;
import com.confession.dto.UserDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.CommentMapper;
import com.confession.mapper.UserMapper;
import com.confession.pojo.Comment;
import com.confession.pojo.User;
import com.confession.request.PostCommentRequest;
import com.confession.service.CommentService;
import com.confession.service.ConfessionPostService;
import com.confession.service.UserService;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.confession.comm.RedisConstant.CONFESSION_PREFIX_LOCK;
import static com.confession.comm.RedisConstant.USER_COMMENT_REPLY;
import static com.confession.comm.ResultCodeEnum.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private ConfessionPostService confessionPostService;

    @Resource
    private SensitiveTextFilter sensitiveTextFilter;

    @Resource
    private WallConfig wallConfig;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public Integer publishCommentReply(PostCommentRequest request, Integer userId) {

        //判断用户是不是可以评论的
        User user = userMapper.selectById(userId);  //检查用户状态是否可以评论
        int userStatus = user.getStatus();
        if (userStatus == 2 || userStatus == 3) {
            throw new WallException(CANNOT_COMMENT);
        }

        //调用方法对敏感字进行匹配，如果匹配到了，直接打回
        Boolean hasSensitiveWords = this.hasSensitiveWords(request.getCommentContent());
        if (hasSensitiveWords) {
            throw new WallException(COMMENT_SENSITIVE_WORD_ALARM);
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
        commentMapper.insert(comment);
        //后面获取记录的时候就获取这个锁了，方式要获取的锁太多，毫秒级别的延迟也可以
        RLock lock = redissonClient.getLock(CONFESSION_PREFIX_LOCK + request.getConfessionPostReviewId());
        lock.lock();

        String key = RedisConstant.POST_SUBMISSION_RECORD + request.getConfessionPostReviewId();
        JSONObject redisDtoTemp = (JSONObject) redisTemplate.opsForValue().get(key);
        ConfessionPostDTO postDTO;
        if (redisDtoTemp != null) { //这里有缓存就更新,这里不为空就是有缓存
            postDTO = redisDtoTemp.toJavaObject(ConfessionPostDTO.class);
            if (request.getParentCommentId() != null) { //这就是子评论
                postDTO.getSubComments().add(createCommentDTO(comment));
            } else {
                postDTO.getMainComments().add(createCommentDTO(comment));
            }
            // 更新缓存
            updateCacheWithExpiration(key, postDTO);
        }
        lock.unlock();

        // 获取插入数据的 ID
        Integer commentId = comment.getId();
        return commentId;
    }

    // 更新缓存并设置过期时间
    private void updateCacheWithExpiration(String key, ConfessionPostDTO postDTO) {
        long expiration = redisTemplate.getExpire(key, TimeUnit.SECONDS);
//        if (expiration < 0) {  //这里逻辑不要了，因为调用的之后key有值，说明key存在
//            // key不存在或没有设置过期时间
//            redisTemplate.opsForValue().set(key, postDTO);
//            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
//        } else
        redisTemplate.opsForValue().set(key, postDTO);
        if (expiration < 24 * 60 * 60) {
            // key的过期时间小于一天
            redisTemplate.expire(key, expiration + 30 * 60, TimeUnit.SECONDS);
        } else {
            // key的过期时间大于等于一天
            redisTemplate.expire(key, expiration, TimeUnit.SECONDS);
        }
    }

    @Override
    public List<CommentDTO> viewRecordsOnId(Integer contentId, boolean isMain) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getConfessionPostReviewId, contentId);
        List<Comment> list = commentMapper.selectList(wrapper);
        List<CommentDTO> comments = new ArrayList<>();
        for (Comment comment : list) {
            CommentDTO commentDTO = createCommentDTO(comment);
            if ((isMain && comment.getParentCommentId() == null) || (!isMain && comment.getParentCommentId() != null)) {
                comments.add(commentDTO);
            }
        }
        return comments;
    }

    private CommentDTO createCommentDTO(Comment comment) {
        UserDTO userDTO = userService.getUserFromRedisOrDatabase(comment.getUserId());
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment, commentDTO);
        commentDTO.setUserName(userDTO.getUsername());
        commentDTO.setAvatarURL(userDTO.getAvatarURL());
        return commentDTO;
    }

    @Override
    public List<CommentDTO> getRepliesToUserComments(Integer userId, PageTool pageTool) {
        JSON json = (JSON) redisTemplate.opsForValue().get(USER_COMMENT_REPLY + userId + ":" + pageTool.getPage());
        if (json != null) {
            return json.toJavaObject(List.class);
        }
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        List<Integer> userPostId = confessionPostService.getUserPostId(userId);
        Page<Comment> page = new Page<>(pageTool.getPage(), pageTool.getLimit());

        queryWrapper.ne(Comment::getUserId, userId)
                .gt(Comment::getCommentTime, LocalDateTime.now().minusMonths(6))
                .inSql(Comment::getParentCommentId, "SELECT Id FROM comment WHERE userId = " + userId)
                .or()
                .orderByDesc(Comment::getCommentTime);
        if (userPostId.size() > 0) {
            queryWrapper.in(Comment::getConfessionPostReviewId, userPostId).ne(Comment::getUserId, userId);  //这里还要加条件
        }
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
        redisTemplate.opsForValue().set(USER_COMMENT_REPLY + userId + ":" +
                pageTool.getPage(), JSONObject.toJSON(comments), 30, TimeUnit.MINUTES);
        return comments;
    }

    @Override
    public int numberUnreadCommentsByUsers(Integer userId, LocalDateTime dateTime) {
        List<Integer> userPostId = confessionPostService.getUserPostId(userId);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        if (userPostId.size() > 0) {
            queryWrapper.in(Comment::getConfessionPostReviewId, userPostId);
        }
        queryWrapper.inSql(Comment::getParentCommentId, "SELECT Id FROM comment WHERE userId = " + userId)
                .gt(Comment::getCommentTime, dateTime)
                .ne(Comment::getUserId, userId);
        Integer count = commentMapper.selectCount(queryWrapper);
        return count;
    }

    @Override
    public Boolean hasSensitiveWords(String text) {
        AhoCorasickDoubleArrayTrie<String> trie = sensitiveTextFilter.getTrie(); // 获取敏感词库的 Trie 实例
        Collection<AhoCorasickDoubleArrayTrie.Hit<String>> hits = trie.parseText(text);
        if (hits.isEmpty()) {
            return false; // 没有敏感词匹配，直接返回 false
        }
        if (hits.size() > 0) {
            return true;
        }
        return false;
    }


}
