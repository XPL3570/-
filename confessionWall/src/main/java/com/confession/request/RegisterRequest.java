package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterRequest {

    private String code;

    private String schoolName;

    private String userName;

    private String avatarUrl;

}
