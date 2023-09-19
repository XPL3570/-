package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterSchoolRequest {

    @NotBlank(message = "学校图标不能为空")
    private String avatarURL;

    @NotBlank(message = "学校名称不能为空")
    private String schoolName;

    @NotBlank(message = "学校描述不能为空")
    private String description;


}
