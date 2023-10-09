package com.confession.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ParameterIntTypeRequest {
    @NotNull(message = "int类型的数字不能是null")
    @Min(value = 0,message = "不能小于0")
    private Integer requestId;
}
