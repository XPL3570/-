package com.confession.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuditRequest {

    //这里校验数字最小是0

    @NotNull(message = "id不能为空") //投稿id
    @Min(value = 0,message = "id不能小于0")
    private Integer id;

    @NotNull(message = "投稿用户id不能为空")
    @Min(value = 0,message = "id不能小于0")
    private Integer postUserId;

    @NotNull(message = "wallId不能为空")
    @Min(value = 0,message = "wallId不能为空不能小于0")
    private Integer wallId;

    @NotNull(message = "postStatus不能为空")
    @Min(value = 0,message = "postStatus不能小于0")
    private Integer postStatus;
}
