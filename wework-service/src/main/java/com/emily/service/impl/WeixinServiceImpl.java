package com.emily.service.impl;

import com.alice.emily.utils.HTTP;
import com.alice.emily.utils.LOG;
import com.alice.emily.utils.logging.Logger;
import com.emily.dto.ArticleDto;
import com.emily.service.SogouService;
import com.emily.service.WeixinService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lianhao on 2017/10/30.
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    @Autowired
    SogouService sogouService;

    private final Logger log = LOG.getLogger(WeixinService.class);

    @Override
    public ArticleDto getArticleByUrl(String url) throws Exception {
        HTTP.HttpRequest request = HTTP.get(url);
        String html = request.body();
        Document doc = Jsoup.parse(html);

        //文章标题
        String title = doc.getElementById("activity-name").addClass("rich_media_title").text();
        //公众号名称
        String name = doc.getElementById("post-user").addClass("rich_media_meta_nickname").text();
        //日期
        String date = doc.getElementById("post-date").addClass("rich_media_meta_text").text();
        ArticleDto articleDto = new ArticleDto();
        try {
            articleDto = sogouService.getArticleInfo(title, name);
        } catch (Exception e) {
            log.error("getArticleInfo error: " + e.getMessage());
        }

        //图片
        List<String> images = new ArrayList<String>();
        Elements elementImages = doc.select("img[data-src]");
        for (Element element : elementImages) {
            images.add(element.attributes().get("data-src"));
        }

        //视频
        List<String> videos = new ArrayList<String>();
        Elements elementVideos = doc.select("iframe[data-src]");
        for (Element element : elementVideos) {
            videos.add(element.attributes().get("data-src"));
        }
        //原文链接
        String source = doc.getElementById("js_sg_bar").children().get(0).attr("href");
        articleDto.setImages(images);
        articleDto.setVideos(videos);
        articleDto.setName(name);
        articleDto.setTitle(title);
        articleDto.setDate(date);
        articleDto.setSource(source);
        return articleDto;
    }
}
