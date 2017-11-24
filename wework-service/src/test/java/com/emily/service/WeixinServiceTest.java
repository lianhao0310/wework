package com.emily.service;

import com.alice.emily.utils.HTTP;
import com.emily.WeworkServiceApplicationTests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Lianhao on 2017/10/31.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeworkServiceApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class WeixinServiceTest {

    @Autowired
    SogouService sogouService;

    @Test
    public void testGetArticleByUrl() {
        String url = "https://mp.weixin.qq.com/s?src=11&timestamp=1509433201&ver=485&signature=aDsQh2SSDUO4D8lmlfNYs6Ul7D1gmTA9jCvsQ-9OxSKdpnZlSZaAdfktrROAyRGTDtpf4vqcNjtlOW7kz2-WaiImbVZRoR4Sj71MFPQrFX0qI2K2jnWKfxr*tVxAWGMi&new=1";
        HTTP.HttpRequest request = HTTP.get(url);
        String html = request.body();
        Document doc = Jsoup.parse(html);
        String title = doc.getElementById("activity-name").addClass("rich_media_title").text();
        String name = doc.getElementById("post-user").addClass("rich_media_meta_nickname").text();

        try {
            sogouService.getArticleInfo(title, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(doc);
    }
}