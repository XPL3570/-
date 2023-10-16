package com.confession.controller;

import com.aliyuncs.exceptions.ClientException;
import com.confession.comm.Result;
import com.confession.globalConfig.exception.WallException;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.request.DeleteImageRequest;
import com.confession.request.UploadImageRequest;
import com.confession.service.ImageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@RestController
public class ImageController {

    @Resource
    private ImageService imageService;
    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("getStsToken")
    public Result getStsToken(){
        //这里加一个锁或者限制，来限制用户获取秘钥等参数， 比如每个小时一个用户最多只能获取18个key
        Integer userId = JwtInterceptor.getUser().getId();
        this.checkFrequency(userId);
        return imageService.uploadImageOOS();
    }

    @PostMapping("/deleteImage")
    public Result deleteImage(@RequestBody @Validated DeleteImageRequest request) {
        Integer userId = JwtInterceptor.getUser().getId();
        this.checkFrequency(userId);
        //这里加一个锁或者限制，来限制用户获取秘钥等参数， 比如每个小时一个用户最多只能获取18个key
        return  imageService.deleteImage(request,userId);
    }
    //校验用户有没有过多的操作图片
    private void checkFrequency(Integer userId){
        // 定义Redis Key，格式为用户ID + 当前小时数
        String redisKey = "user_oss_upload:" + userId;
        // 从Redis中获取当前用户已获取的key数量
        Integer count = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (count == null) {
            // 如果不存在该Key，则初始化为0
            count = 0;
        }
        if (count >= 18) {
            // 如果已达到限制，返回错误信息或抛出异常
          throw new WallException("操作图片超过限制，请稍后重试",244);
        }
        // 计数器加1
        count++;
        // 将新的计数器值存入Redis，设置过期时间为下一个小时
        redisTemplate.opsForValue().set(redisKey, count, Duration.ofHours(1));
    }


    @GetMapping("/admin/getStsToken")
    public Result getStsTokenAdmin(){
        Result res;
        //这里加一个锁或者限制，来限制用户获取秘钥等参数，一个小时最多100个
        // 定义Redis Key，格式为用户ID + 当前小时数
        String redisKey = "user_oss_upload_admin:";
        // 从Redis中获取当前用户已获取的key数量
        Integer count = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (count == null) {
            // 如果不存在该Key，则初始化为0
            count = 0;
        }
        if (count >= 100) {
            // 如果已达到限制，返回错误信息或抛出异常
            throw new WallException("操作图片超过限制，请稍后重试",244);
        }
        // 计数器加1
        count++;
        // 将新的计数器值存入Redis，设置过期时间为下一个小时
        redisTemplate.opsForValue().set(redisKey, count, Duration.ofHours(1));
        try {
           res= imageService.uploadImageOOSWeb();
        } catch (ClientException e) {
            return Result.fail();
        }
        if (res!=null){
            return res;
        }else {
            return Result.fail();
        }
    }







//    @PostMapping("/admin/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        return imageService.upload(file);
    }


//    @PostMapping("/uploadImage")
    public Result uploadImage(@RequestBody UploadImageRequest request) {
      return imageService.uploadImage(request);
    }


}
