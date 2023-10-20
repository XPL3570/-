package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SubReportRecordRequest {

    /**
     * 举报投稿id
     */
    @NotNull(message = "id不能为空")
    private Integer reportId;

    @NotBlank(message = "举报建议不能是空")
    private String message;



}
