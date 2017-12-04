package com.emily.service.impl;

import com.alice.emily.utils.HTTP;
import com.alice.emily.utils.LOG;
import com.alice.emily.utils.URLUtils;
import com.alice.emily.utils.logging.Logger;
import com.emily.dto.ArticleDto;
import com.emily.service.SogouService;
import com.emily.service.WeixinService;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
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
            String image = element.attributes().get("data-src");
            images.add(image);
        }

        //视频
        List<String> videos = new ArrayList<String>();
        Elements elementVideos = doc.select("iframe[data-src]");
        for (Element element : elementVideos) {
            videos.add(element.attributes().get("data-src"));
        }

        //原文链接
        if (doc.getElementById("js_sg_bar").children().size() > 0) {
            String source = doc.getElementById("js_sg_bar").children().get(0).attr("href");
            articleDto.setSource(source);
        }

        if (images.size()>0){
            articleDto.setImages(images);
        }
        if(videos.size()>0){
            articleDto.setVideos(videos);
        }
        articleDto.setName(name);
        articleDto.setTitle(title);
        articleDto.setDate(date);
        return articleDto;
    }

    @Override
    public byte[] getThumbsByUrl(@NotNull String url) throws Exception {

        String fmt = "jpg";
        String[] querys = URLUtils.getURL(url).getQuery().split("&");
        if (querys.length > 0) {
            for (String query : querys) {
                if (query.startsWith("wx_fmt=")) {
                    fmt = query.replace("wx_fmt=", "");
                    break;
                }
            }
        }

        byte[] data;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new URL(url))
                .scale(1.0d)
                .outputFormat(fmt)
                .imageType(BufferedImage.TYPE_INT_ARGB)
                .toOutputStream(outputStream);

        outputStream.flush();
        data = outputStream.toByteArray();
        outputStream.close();
        return data;
    }
}
