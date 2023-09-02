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
}
