package com.emily.service;

import com.emily.dto.ArticleDto;

/**
 * Created by Lianhao on 2017/10/30.
 */
public interface WeixinService {
    ArticleDto getArticleByUrl(String url) ;

    byte[] getThumbsByUrl(String url) throws Exception;
}
