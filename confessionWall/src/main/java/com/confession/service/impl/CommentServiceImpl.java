package com.confession.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.confession.config.WallConfig;
import com.confession.dto.CommentDTO;
import com.confession.dto.UserDTO;
import com.confession.globalConfig.exception.WallException;
import com.confession.mapper.CommentMapper;
import com.confession.pojo.Comment;
import com.confession.request.PostCommentRequest;
import com.confession.service.CommentService;
import com.confession.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.confession.comm.ResultCodeEnum.COMMENT_OVER_LIMIT;
import static com.confession.comm.ResultCodeEnum.CONTRIBUTE_OVER_LIMIT;

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
    private WallConfig wallConfig;

    @Override
    public Integer publishCommentReply(PostCommentRequest request, Integer userId) {

        //todo  将来可能会把这个评论的发表直接同步到缓存,用户信息会上缓存

        int count = commentMapper.getCommentCountByUserIdAndDate(userId, LocalDate.now());
        System.out.println(count);
        if (count>=wallConfig.getUserDailyCommentLimit()){  //判断是否超过每天评论限制
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
        wrapper.eq(Comment::getConfessionPostReviewId,contentId);
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


}
