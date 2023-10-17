package com.confession;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.confession.mapper.UserMapper;
import com.confession.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ConfessionWallApplicationTests {

//    @Resource
//    private UserMapper userMapper;

    /**
     * 测试可以可以通过设置逻辑删除字段来查询已经逻辑删除的数据    不能
     */
//    @Test
    void contextLoads() {
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(User::getDeleted,true);
//        userMapper.selectList(wrapper);
    }

}
