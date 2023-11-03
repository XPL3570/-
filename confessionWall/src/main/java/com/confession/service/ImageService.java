package com.confession.service;

import com.aliyuncs.exceptions.ClientException;
import com.confession.comm.Result;
import com.confession.request.DeleteImageRequest;
import com.confession.request.UploadImageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    /**
     * 官网的阿里云直传服务器签名接口
     */
    Result alibabaCloudDirectServerSignature() throws ClientException;

    /**
     * 删除图片
     */
    Result deleteImage(DeleteImageRequest request,Integer userId);


    /**
     *  上传图片
     * @return 地址  弃用了
     */
    Result upload(MultipartFile file);

    /**
     *  上传图片  传递的是base64的数组 弃用了
     * @return
     */
    Result uploadImage(UploadImageRequest request);


    /**
     * 使用阿里云上传图片获取临时票据 ,也需要token 弃用了
     */
    Result uploadImageOOS();

    /**
     * 管理web端使用阿里云上传图片获取临时票据 ,也需要token  弃用了
     */
    Result uploadImageOOSWeb();



}
