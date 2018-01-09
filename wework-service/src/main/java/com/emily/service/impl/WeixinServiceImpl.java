package com.emily.service.impl;

import com.alice.emily.utils.HTTP;
import com.alice.emily.utils.LOG;
import com.alice.emily.utils.URLUtils;
import com.alice.emily.utils.logging.Logger;
import com.emily.dto.ArticleDto;
import com.emily.service.SogouService;
import com.emily.service.WeixinService;
import com.emily.utils.UrlUtils;
import com.google.common.base.Preconditions;
import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
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
import java.util.Arrays;
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
    public ArticleDto getArticleByUrl(String url) {
        Preconditions.checkArgument(url.contains("mp.weixin.qq.com"), "Article must from mp.weixin.qq.com !");
        HTTP.HttpRequest request = HTTP.get(url);
        Preconditions.checkArgument(request.code() == 200, "get article from wx error : " + url);
        String html = request.body();
        Document doc = Jsoup.parse(html);
        ArticleDto articleDto = new ArticleDto();
        //文章标题
        if (doc.getElementById("activity-name") != null) {
            String title = doc.getElementById("activity-name").addClass("rich_media_title").text();
            articleDto.setTitle(title);
        }

        //公众号名称
        if (doc.getElementById("post-user") != null) {
            String name = doc.getElementById("post-user").addClass("rich_media_meta_nickname").text();
            articleDto.setName(name);
        }
        //日期
        if (doc.getElementById("post-date") != null) {
            String date = doc.getElementById("post-date").addClass("rich_media_meta_text").text();
            articleDto.setDate(date);
        }

        /*取得script下面的JS变量*/
        Elements jsElements = doc.getElementsByTag("script");
        if (jsElements != null && jsElements.size() > 0) {
            for (Element jsElement : jsElements) {
             /*取得JS变量数组*/
                String[] data = jsElement.data().toString().split("var");
                if (Arrays.asList(data).toString().contains("msg_cdn_url")) {
                /*取得单个JS变量*/
                    for (String variable : data) {
                    /*过滤variable为空的数据*/
                        if (variable.contains("=")) {
                        /*取到满足条件的JS变量, 封面*/
                            if (variable.contains("msg_cdn_url")) {
                                String[] kvp = variable.split("=");
                                String cover = kvp[1].trim().substring(0, kvp[1].trim().length() - 1).toString().replace("\"", "");
                                if (!StringUtil.isBlank(cover)) {
                                    articleDto.setCover(cover);
                                }

                            }
                            //摘要
                            if (variable.contains("msg_desc")) {
                                String[] kvp = variable.split("=");
                                String desc = kvp[1].trim().substring(0, kvp[1].trim().length() - 1).toString().replace("\"", "");
                                if (desc != null) {
                                    articleDto.setDigest(desc);
                                }
                            }
                            //原文链接
                            if (variable.contains("msg_source_url")) {
                                String[] kvp = variable.split("=");
                                String source = kvp[1].trim().substring(0, kvp[1].trim().length() - 1).toString().replace("\"", "");
                                if (source != null) {
                                    articleDto.setSource(source);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        //图片
        List<String> images = new ArrayList<String>();
        Elements elementImages = doc.select("img[data-src]");
        if (elementImages != null && elementImages.size() > 0) {
            for (Element imgElement : elementImages) {
                String image = imgElement.attributes().get("data-src");
                images.add(image);
            }
            articleDto.setImages(images);
        }

        //视频
        List<String> videos = new ArrayList<String>();
        Elements elementVideos = doc.select("iframe[data-src]");
        if (elementVideos != null && elementVideos.size() > 0) {
            for (Element videoElement : elementVideos) {
                String videoUrl = videoElement.attributes().get("data-src");
                String vid = UrlUtils.getUrlQuery(videoUrl, "vid");
                if (!StringUtil.isBlank(vid)) {
                    videos.add("https://v.qq.com/x/page/" + vid + ".html");
                }
            }
            articleDto.setVideos(videos);
        }
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
