package com.emily.controller;

import com.alice.emily.utils.LOG;
import com.alice.emily.utils.URLUtils;
import com.alice.emily.utils.logging.Logger;
import com.emily.constant.Constants;
import com.emily.domain.ResponseData;
import com.emily.dto.ArticleDto;
import com.emily.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.URL;

/**
 * Created by Lianhao on 2017/10/30.
 */
@Api(tags = "weixin")
@Validated
@RestController
@RequestMapping("weixin")
public class WeixinController {

    static final Logger log = LOG.getLogger(WeixinController.class);

    @Autowired
    WeixinService weixinService;

    /**
     * 获取文章信息
     *
     * @param url
     */
    @ApiOperation("获取文章信息")
    @GetMapping("article")
    public ResponseData<ArticleDto> getArticle(@RequestParam("url") @NotNull String url) {
        return new ResponseData(weixinService.getArticleByUrl(url));
    }

    @ApiOperation("根据Url获取图片")
    @GetMapping("/image")
    @ResponseBody
    public void getImg(@RequestParam(value = "url") @NotNull String url, HttpServletResponse response) {
        try {
            String fmt = "jpg";
            if (URLUtils.getURL(url).getQuery() != null) {
                String[] querys = URLUtils.getURL(url).getQuery().split("&");
                for (String query : querys) {
                    if (query.startsWith("wx_fmt=")) {
                        fmt = query.replace("wx_fmt=", "");
                        break;
                    }
                }
            } else {
                String[] querys = url.split("/");
                if (querys[querys.length - 1].contains(".")) {
                    String query = querys[querys.length - 1];
                    String[] fmts = query.split("\\.");
                    fmt = fmts[1];
                }
            }

            if (!("image." + fmt).matches(Constants.IMAGE_PATTERN)) {
                fmt = "jpg";
            }
            response.setHeader("content-disposition", "attachment;filename=image." + fmt);
            DataOutputStream dout = new DataOutputStream(response.getOutputStream());
            BufferedImage image = ImageIO.read(new URL(url));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(image, fmt, out);
            byte[] b = out.toByteArray();
            dout.write(b);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
