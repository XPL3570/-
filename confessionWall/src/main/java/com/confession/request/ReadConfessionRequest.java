package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReadConfessionRequest {
    @NotNull(message = "墙id不能是null")
    private Integer wallId;

    private Long recordAfterTime;

    private Integer pageSize;
}
