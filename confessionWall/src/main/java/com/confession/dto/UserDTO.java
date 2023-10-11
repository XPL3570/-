package com.confession.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像地址
     */
    private String avatarURL;



}
