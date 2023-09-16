package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class UploadImageRequest {


    @NotBlank(message = "图片信息不能是null")
    private String base64Image;

}
