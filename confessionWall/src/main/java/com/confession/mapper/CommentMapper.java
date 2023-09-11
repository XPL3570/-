package com.confession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.confession.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询用户当天的发布数量
     * @param userId
     * @param date
     * @return
     */
    int getCommentCountByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

}
