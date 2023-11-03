package com.confession.comm;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * 要放入缓存zSet的记录id，有时间戳
 */
@Data
public class RecordIDCache {
    private Integer id;

    private LocalDateTime time;

    private static Random random;

    static {
        random = new Random();
    }

    public Long getTimeInMillis() {
        long timestamp = time.toInstant(ZoneOffset.UTC).toEpochMilli();
        int randomNumber = random.nextInt(1000);
//        System.out.println("生产的时间戳："+(timestamp + randomNumber));
        return timestamp + randomNumber;
    }
}
