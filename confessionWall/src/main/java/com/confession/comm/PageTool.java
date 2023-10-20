package com.confession.comm;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PageTool {


    @Min(value = 1, message = "小于最小值")
    @Max(value = 30, message = "超过最大值")
    private int page = 1;

    @Min(value = 1, message = "小于最小值")
    @Max(value = 10000, message = "超过最大值")
    private int limit = 10;

}

