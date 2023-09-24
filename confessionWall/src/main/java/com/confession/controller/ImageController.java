package com.confession.controller;

import com.confession.comm.Result;
import com.confession.request.DeleteImageRequest;
import com.confession.request.UploadImageRequest;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@RestController
public class ImageController {

    private static final String UPLOAD_PATH = "e:\\表白墙项目图片上传地址\\"; // 设置图片上传路径，这里是windows系统的

    private static final String DOMAIN_NAME_ADDRESS="http://127.0.0.1:2204/upload/";  //后面可以改成读取配置文件，设置成域名

    @PostMapping("/deleteImage")
    public Result deleteImage(@RequestBody @Validated DeleteImageRequest request) {
        try {
            // 提取图片的相对路径或文件名
            String imagePath = getImagePathFromUrl(request.getDeleteUrl());

            // 构建图片的完整路径
            String fullPath = UPLOAD_PATH + imagePath;

            File imageFile = new File(fullPath);
            if (imageFile.exists()) { // 判断图片文件是否存在
                if (imageFile.delete()) { // 删除图片文件
                    return Result.ok("删除成功");
                } else {
                    return Result.fail("删除失败");
                }
            } else {
                return Result.fail("图片不存在");
            }
        } catch (Exception e) {
            return Result.fail("删除异常：" + e.getMessage());
        }
    }
    private String getImagePathFromUrl(String imageUrl) {
        try {
            // 去掉域名和端口部分，只保留相对路径或文件名
            return imageUrl.replace(DOMAIN_NAME_ADDRESS, "");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid image URL: " + imageUrl);
        }
    }



    @PostMapping("/uploadImage")
    public Result uploadImage(@RequestBody UploadImageRequest request) {
        try {
            // 将Base64字符串解码为字节数组
            byte[] imageBytes = Base64Utils.decodeFromString(request.getBase64Image());

            // 检查图片大小是否超过2MB
            if (imageBytes.length > 2 * 1024 * 1024) {
                return Result.build(400, "上传的图片大小超过2MB");
            }

            // 获取图片后缀
            String fileExtension = getImageExtensionFromBase64(request.getBase64Image());
            if(fileExtension==null||fileExtension==""){
                fileExtension="png";
            }

            // 根据日期和随机数生成文件名，并加上文件扩展名
            String fileName = generateFileName("."+fileExtension);

            // 构建文件对象
            File file = new File(UPLOAD_PATH, fileName);
            // 将字节数组写入文件
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageBytes);
            }
            // 返回图片地址
            return Result.ok(DOMAIN_NAME_ADDRESS + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.build(500, "图片上传失败");
        }
    }
    private String generateFileName(String fileExtension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePrefix = dateFormat.format(new Date());
        String randomSuffix = generateRandomSuffix(6); // 生成6位随机数或英文字母
        return datePrefix + randomSuffix + fileExtension;
    }
    private String generateRandomSuffix(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int type = random.nextInt(2);
            switch (type) {
                case 0:
                    sb.append((char) (random.nextInt(26) + 'a')); // 生成小写字母
                    break;
                case 1:
                    sb.append((char) (random.nextInt(26) + 'A')); // 生成大写字母
                    break;
            }
        }
        return sb.toString();
    }

    public static String getImageExtensionFromBase64(String base64Image) {
        // 解码Base64图片信息为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // 读取图片的前几个字节
        byte[] headerBytes = new byte[4];
        System.arraycopy(imageBytes, 0, headerBytes, 0, 4);

        // 判断图片格式并获取后缀名
        String extension = "";
        if (headerBytes[0] == (byte) 0xFF && headerBytes[1] == (byte) 0xD8 && headerBytes[2] == (byte) 0xFF) {
            extension = "jpg";
        } else if (headerBytes[0] == (byte) 0x89 && headerBytes[1] == (byte) 0x50 && headerBytes[2] == (byte) 0x4E) {
            extension = "png";
        } else if (headerBytes[0] == (byte) 0x47 && headerBytes[1] == (byte) 0x49 && headerBytes[2] == (byte) 0x46) {
            extension = "gif";
        } else if (headerBytes[0] == (byte) 0x42 && headerBytes[1] == (byte) 0x4D) {
            extension = "bmp";
        }
        return extension;
    }

}
