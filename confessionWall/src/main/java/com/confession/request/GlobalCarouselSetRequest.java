package com.confession.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GlobalCarouselSetRequest {

    private List<String> carouselList;

    @NotNull(message = "禁用状态不能是空")
    private Boolean carouselIsDisabled;

}
