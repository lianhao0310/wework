package com.emily.params;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Lianhao on 2017/9/25.
 */
@Data
public class ThumbParameter {
    @NotNull
    private String path;
    private Integer width;
    private Integer height;
}
