package com.confession.mapper;

import com.confession.pojo.Lottery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年09月16日
 */
@Mapper
public interface LotteryMapper extends BaseMapper<Lottery> {

    /**
     * 加mysql锁的方式，这里加一个java的锁吧，本来就递归，然后再加一个表锁怕性能差
     * @param gender
     * @param schoolId
     * @param drawCount
     * @return
     */
    @Select("SELECT * FROM lottery WHERE Gender = #{gender} AND SchoolId = #{schoolId} AND DrawCount = #{drawCount} FOR UPDATE")
    List<Lottery> selectListWithLock(@Param("gender") int gender, @Param("schoolId") int schoolId, @Param("drawCount") int drawCount);

    int countYearlyRecords(int userId);
    int countMonthlyRecords(int userId);
    int countDailyRecords(int userId);
    int countTotalRecords(int userId);


}
