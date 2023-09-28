package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserStatusModRequest {
    @NotNull(message = "用户id不能是null")
    private Integer userId;

    @NotNull(message = "修改状态不能为空")
    @Min(value = 0, message = "修改状态只能是0-3")
    @Max(value = 3, message = "修改状态只能是0-3")
        private Integer status;
}
