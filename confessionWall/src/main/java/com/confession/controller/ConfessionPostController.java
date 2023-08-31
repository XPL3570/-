package com.confession.controller;


import com.confession.comm.Result;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.pojo.Confessionpost;
import com.confession.request.ConfessionPostRequest;
import com.confession.service.ConfessionpostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023年08月20日
 */
@RestController
@RequestMapping("/api/confessionPost")
public class ConfessionPostController {


    @Resource
    private ConfessionpostService confessionpostService;

    @PostMapping("/submit")
    public Result submitConfessionWall(@RequestBody ConfessionPostRequest confessionRequest) {
        // 判断是否包含图片
        boolean hasImage = (confessionRequest.getImageURL() != null && !confessionRequest.getImageURL().isEmpty());

        // 设置状态值，默认为0（待审核）
        int status = 0;

        // 如果不包含图片，则执行过滤并设置状态为1（通过） todo
        if (!hasImage) {
            boolean filterResult = filterContent(confessionRequest);
            if (filterResult) {
                status = 1;
            }
        }
        // 构建ConfessionWall对象
        Confessionpost confessionPost = new Confessionpost();
        if (status==1){
            confessionPost.setPublishTime(LocalDateTime.now());
        }

        Integer id = JwtInterceptor.getUser().getId();
       
        confessionPost.setUserId(id);
        confessionPost.setImageURL(confessionRequest.getImageURL());
        confessionPost.setTitle(confessionRequest.getTitle());
        confessionPost.setTextContent(confessionRequest.getTextContent());
        confessionPost.setPostStatus(status);

        // 保存ConfessionWall对象到数据库
        confessionpostService.save(confessionPost);

        // 返回结果
        String message = (status == 1) ? "发布成功" : "等待管理员审核";
        return Result.build(200, message);
    }

    private boolean filterContent(ConfessionPostRequest confessionRequest) {
        // 执行过滤逻辑，根据你的需求进行实现
        // 返回过滤结果，true表示通过，false表示未通过
        // todo
        return true;
    }

}

