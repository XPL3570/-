package com.confession.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像地址
     */
    private String avatarURL;



}
