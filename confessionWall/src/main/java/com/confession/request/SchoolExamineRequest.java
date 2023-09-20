package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class SchoolExamineRequest {
    @NotNull(message = "学校id不能为空")
    private Integer schoolId;

    @NotNull(message = "IsVerified不能为空")
    @Min(value = 1, message = "IsVerified的值只能为1或2")
    @Max(value = 2, message = "IsVerified的值只能为1或2")
    private Integer IsVerified;

}
