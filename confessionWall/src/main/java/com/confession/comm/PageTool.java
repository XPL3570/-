package com.confession.comm;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
@Data
@AllArgsConstructor
public class PageTool {
    private Integer pageNo;
    private Integer pageSize;

    public static Page ofDefault() {
        return new Page(1, 10);
    }

    public Page buildPage(){
        Integer effectivePageNo = pageNo != null ? pageNo : 1;
        Integer effectivePageSize = pageSize != null ? pageSize : 10;

        return new Page(effectivePageNo, effectivePageSize);
    }

}

