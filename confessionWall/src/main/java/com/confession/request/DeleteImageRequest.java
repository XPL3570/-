package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeleteImageRequest {

    @NotBlank(message = "删除地址不能是空")
    private String deleteUrl;

}
