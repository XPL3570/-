package com.confession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.confession.pojo.Confessionwall;
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
public interface ConfessionwallMapper extends BaseMapper<Confessionwall> {

    /**
     * 查询学校id和墙id是否有记录数
     * @param schoolId
     * @param wallId
     * @return
     */
    Integer isWallInSchool(@Param("schoolId") Integer schoolId, @Param("wallId") Integer wallId);



}
