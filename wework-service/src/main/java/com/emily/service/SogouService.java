package com.emily.service;

import com.emily.dto.ArticleDto;

/**
 * Created by Lianhao on 2017/10/31.
 */
public interface SogouService {
    ArticleDto getArticleInfo(String title, String name)  throws Exception ;
}
