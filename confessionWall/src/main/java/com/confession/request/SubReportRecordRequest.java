package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SubReportRecordRequest {

    /**
     * 举报投稿id
     */
    @NotNull(message = "id不能为空")
    private Integer reportId;

    @NotBlank(message = "举报建议不能是空")
    @Size(max = 63, message = "举报建议最长只能63个字")
    private String message;


}
