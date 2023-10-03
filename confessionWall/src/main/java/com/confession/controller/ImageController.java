package com.confession.controller;

import com.confession.comm.Result;
import com.confession.globalConfig.interceptor.JwtInterceptor;
import com.confession.request.DeleteImageRequest;
import com.confession.request.UploadImageRequest;
import com.confession.service.ImageService;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@RestController
public class ImageController {

    @Resource
    private ImageService imageService;



    @PostMapping("/deleteImage")
    public Result deleteImage(@RequestBody @Validated DeleteImageRequest request) {
        return  imageService.deleteImage(request);
    }


    @PostMapping("/admin/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        return imageService.upload(file);
    }


    @PostMapping("/uploadImage")
    public Result uploadImage(@RequestBody UploadImageRequest request) {
      return imageService.uploadImage(request);
    }


}
