package com.confession.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


@Data
@EqualsAndHashCode(callSuper = false)
public class LoginRequest {

    @NotBlank(message = "code不能是null")
    private String code;

}
