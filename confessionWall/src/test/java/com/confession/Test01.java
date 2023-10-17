package com.confession;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class Test01 {

//    @Resource
    private RedissonClient redissonClient;

//    @Test
    public void test01(){
        System.out.println(redissonClient.toString());
//        RLock ss = redissonClient.getLock("ss");
    }

//    @Test
    public void test02(){
        log.info("This is a log message.");
        log.error("错误提示");
    }
}
