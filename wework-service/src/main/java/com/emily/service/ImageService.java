package com.emily.service;


import com.emily.params.ThumbParameter;

/**
 * Created by Lianhao on 2017/9/22.
 */
public interface ImageService {
    byte[] getThumbByPath(ThumbParameter parameter) throws Exception;


}
