package com.confession.comm;

import lombok.Data;

/**
 * 要放入缓存zSet的记录id，有时间戳
 */
@Data
public class RecordIDCache {
    Integer id;

    Long timeStamp;


}
