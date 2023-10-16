package com.confession.service;

import com.aliyuncs.exceptions.ClientException;
import com.confession.comm.Result;
import com.confession.request.DeleteImageRequest;
import com.confession.request.UploadImageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    /**
     * 删除图片
     */
    Result deleteImage(DeleteImageRequest request,Integer userId);


    /**
     *  上传图片
     * @return 地址
     */
    Result upload(MultipartFile file);

    /**
     *  上次图片  传递的是base64的数组
     * @return
     */
    Result uploadImage(UploadImageRequest request);



    /**
     * 使用阿里云上传图片获取临时票据 ,也需要token
     */
    Result uploadImageOOS();

    /**
     * 管理web端使用阿里云上传图片获取临时票据 ,也需要token
     */
    Result uploadImageOOSWeb() throws ClientException;

}
