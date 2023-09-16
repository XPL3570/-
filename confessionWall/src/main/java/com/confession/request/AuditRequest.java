package com.confession.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuditRequest {

    @NotNull(message = "id不能为空")
    private Integer id;

    @NotNull(message = "wallId不能为空")
    private Integer wallId;

    @NotNull(message = "postStatus不能为空")
    private Integer postStatus;
}
