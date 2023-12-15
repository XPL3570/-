package com.confession.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    //文件上传 没有调用 测试的
    String fileUpload(MultipartFile file) throws Exception;
}
