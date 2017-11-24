package com.emily.service.impl;

import com.alice.emily.utils.HTTP;
import com.alice.emily.utils.URLUtils;
import com.emily.dto.ArticleDto;
import com.emily.service.SogouService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lianhao on 2017/10/31.
 */
@Service
public class SogouServiceImpl implements SogouService {


    @Override
    public ArticleDto getArticleInfo(String title, String name) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "2");
        params.put("s_from", "inpuut");
        params.put("query", title);
        params.put("ie", "utf8");
        params.put("_sug_", "n");
        HTTP.HttpRequest request = HTTP.get("http://weixin.sogou.com/weixin", params, true);
        String html = request.body();
        Document doc = Jsoup.parse(html);
        ArticleDto articleDto = new ArticleDto();
        Elements elements = doc.getElementsByClass("news-list").get(0).children();
        for (Element element:elements) {
            if(element.getElementsByTag("h3").text().equals(title)) {
                //获取articleId
                String articleId = element.attr("id");

                String[] articleIds = articleId.split("_box_");
                //获取元素id前缀
                String idPrefix = articleIds[0];
                //获取id后缀
                String idSuffix = articleIds[1];

                if (null != idSuffix) {
                    //根据sogou获取封面图片地址
                    String cover = doc.getElementById(idPrefix + "_img_" + idSuffix).getElementsByTag("img").attr("src");
                    String[] querys = URLUtils.getURL(cover).getQuery().split("&");

                    for (String query:querys) {
                        if (query.startsWith("url=")) {
                            //根据sogou图片地址url参数获取封面图片
                            cover = query.replace("url=", "");
                            break;
                        }
                    }

                    //获取简介
                    String digest = doc.getElementById(idPrefix + "_summary_" + idSuffix).text();
                    articleDto.setCover(cover);
                    articleDto.setDigest(digest);
                }
                return articleDto;
            }
        }
        return articleDto;
    }
}
