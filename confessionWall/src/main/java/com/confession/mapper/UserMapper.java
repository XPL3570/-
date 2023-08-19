package com.confession.mapper;

import com.confession.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2023年08月19日
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
