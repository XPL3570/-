package com.confession.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class WallConfig {

    @Value("${wall.admin.daily-post-limit}")
    private int adminDailyPostLimit;

    @Value("${wall.user.daily-post-limit}")
    private int userDailyPostLimit;

    @Value("${wall.user.comment-limit}")
    private int userDailyCommentLimit;

//    @Value("${wall.lottery.limit.yearly}")
//    private int yearlyLimit;
//
//    @Value("${wall.lottery.limit.monthly}")
//    private int monthlyLimit;
//
//    @Value("${wall.lottery.limit.daily}")
//    private int dailyLimit;
//
//    @Value("${wall.lottery.limit.total}")
//    private int totalLimit;

    @Value("${wall.lottery.strategy}")
    private String strategyString;
}
