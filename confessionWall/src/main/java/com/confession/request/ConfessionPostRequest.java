package com.confession.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConfessionPostRequest {
    private Integer wallId;
    private String title;
    private String textContent;
    private String imageURL;
    private Integer isAnonymous;
}
