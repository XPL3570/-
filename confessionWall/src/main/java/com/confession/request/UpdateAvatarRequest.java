package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateAvatarRequest {
    private String avatarUrl;

}