package com.emily.service;

import com.alice.emily.utils.HTTP;
import com.alice.emily.utils.LOG;
import com.alice.emily.utils.logging.Logger;
import com.emily.WeworkServiceApplicationTests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Lianhao on 2017/10/31.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeworkServiceApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class WeixinServiceTest {

    private final Logger log = LOG.getLogger(WeixinServiceTest.class);

    @Autowired
    private SogouService sogouService;

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

    @Test
    public void getFileByUrL() {
        String url = "http://img01.sogoucdn.com/net/a/04/link?appid=100520033&url=http%3A%2F%2Fmmbiz.qpic.cn%2Fmmbiz_jpg%2FibQ2cXpBDzUOjhrNibyhzVbazlBicrwfW3u6Wr3P4FAquricU9enDiawfdnQmRWzRvibzwyzZ3V6z4dSj6q6DMqI5YBw%2F0%3Fwx_fmt%3Djpeg";
        String path = "/opt/weixin/test.png";
        HTTP.HttpRequest request = HTTP.get(url);
        if (request.code() == 200) {
            byte[] result = request.bytes();
            BufferedOutputStream bw = null;
            try {
                // 创建文件对象
                File f = new File(path);
                // 创建文件路径
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                // 写入文件
                bw = new BufferedOutputStream(new FileOutputStream(path));
                bw.write(result);
            } catch (Exception e) {
                log.error("保存文件错误,path=" + path + ",url=" + url, e);
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (Exception e) {
                    log.error("finally BufferedOutputStream shutdown close", e);
                }
            }
        }
    }
}