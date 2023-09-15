package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuditRequest {
    private Integer id;
    private Integer wallId;
    private Integer postStatus;
}
