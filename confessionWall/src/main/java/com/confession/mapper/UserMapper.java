package com.confession.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.confession.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者 xpl
 * @since 2023年08月20日
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
