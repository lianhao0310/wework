package com.emily.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Lianhao on 2017/10/31.
 */
@Data
public class ArticleDto {
    private String name;
    private String title;
    private String date;
    private String cover;
    private String digest;
    private String source;
    private List<String> images;
    private List<String> videos;

}
