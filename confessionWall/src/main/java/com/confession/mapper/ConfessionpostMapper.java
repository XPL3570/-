package com.confession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.confession.comm.RecordIDCache;
import com.confession.pojo.Confessionpost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者 xpl
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

    /**
     * 获取redis里面没有的数据库里面的id和时间戳的集合
     * @param wallId
     * @param startIndex
     * @param limit
     * @return
     */
    List<RecordIDCache> getConfessionPostIDsByWallId(
            @Param("wallId") Integer wallId,
            @Param("startIndex") Integer startIndex,
            @Param("limit") Integer limit
    );





}
