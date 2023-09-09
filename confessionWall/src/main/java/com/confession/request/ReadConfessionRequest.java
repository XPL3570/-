package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReadConfessionRequest {
    private Integer wallId;

    private Long recordAfterTime;

    private Integer pageSize;
}
