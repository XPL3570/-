package com.confession.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Controller
public class FileUploadController {

    // 获取配置文件中指定的存储路径
    @Value("${avatar.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAvatar(@RequestBody String base64Image) {
        try {
            // 解码base64编码的图片数据为字节数组
            byte[] imageBytes = Base64Utils.decodeFromString(base64Image);

            // 生成唯一的文件名
            String fileName = UUID.randomUUID().toString() + ".jpg";

            // 构造文件存储路径及文件对象
            String filePath = uploadPath + File.separator + fileName;
            File imageFile = new File(filePath);

            // 将字节数组写入文件
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(imageBytes);
            }

            // 构造图片的URL地址
            String imageUrl = "127.0.0.1:/uploads/" + fileName;

            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload avatar.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
