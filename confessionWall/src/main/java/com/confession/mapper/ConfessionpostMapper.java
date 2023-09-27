package com.confession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.confession.pojo.Confessionpost;
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
public interface ConfessionpostMapper extends BaseMapper<Confessionpost> {

    /**
     * 查询用户当天的发布数量
     * @param userId
     * @param date
     * @return
     */
    int getPostCountByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    /**
     * 查询超级管理员发布的所有人都可以看到的投稿数量
     * @param date
     * @return
     */
    int getAdminPostCount(@Param("date") LocalDate date);



}
