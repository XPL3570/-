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

    @Value("${wall.user.can-add-friends-month}")
    private int canAddFriendsMonth;

    @Value("${wall.user.can-accept-friends-month}")
    private int canAcceptFriendsMonth;

    @Value("${wall.lottery.strategy}")
    private String strategyString;
}
