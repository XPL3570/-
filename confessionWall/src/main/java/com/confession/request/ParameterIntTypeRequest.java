package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ParameterIntTypeRequest {
    @NotNull(message = "int类型的数字不能是null")
    private Integer requestId;
}
