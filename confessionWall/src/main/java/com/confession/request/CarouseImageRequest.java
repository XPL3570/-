package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CarouseImageRequest {
    @NotNull(message = "Id不能是空")
    private Integer id;
}
