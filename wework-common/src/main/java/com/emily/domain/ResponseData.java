package com.emily.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Lianhao on 2017/9/5.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData<T> {
    private Boolean status;
    private Integer code;
    private String message;
    private T data;

    public ResponseData(T t) {
        this.status = true;
        this.code = 200;
        this.message = "ok";
        this.data = t;
    }
}
