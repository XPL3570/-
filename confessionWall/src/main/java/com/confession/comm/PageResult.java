package com.confession.comm;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> data;
    private Integer count;
    private Long total;

    public PageResult(List<T> data, long total,int count) {
        this.data = data;
        this.total = total;
        this.count=count;
    }
}
