package com.confession.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IndexInfoDTO implements Serializable {

    //表白墙头像
//    private String indexAvatarURL;

    //学校里面的轮播图,加上管理端设置的
    private List<String> CarouselImages;

    //首页提示语
    private String Prompt;


}
