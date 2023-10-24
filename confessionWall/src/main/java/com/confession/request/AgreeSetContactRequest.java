package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AgreeSetContactRequest {

    @NotNull(message = "id不能是null")
    private Integer userContactId;

    @NotNull(message = "同意状态不能是null")
    private Boolean isAgree;

}
