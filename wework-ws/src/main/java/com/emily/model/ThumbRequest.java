package com.emily.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Lianhao on 2017/11/30.
 */
@Data
public class ThumbRequest {
    @NotNull(message = "path must be provided !")
    private String path;
    private Integer width;
    private Integer height;
}
