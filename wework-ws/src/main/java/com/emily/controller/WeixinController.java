package com.emily.controller;

import com.alice.emily.utils.LOG;
import com.alice.emily.utils.URLUtils;
import com.alice.emily.utils.logging.Logger;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
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
    @ApiIgnore
    public ResponseData<ArticleDto> getArticle(@RequestParam("url") @NotNull String url) {
        try {
            return new ResponseData(weixinService.getArticleByUrl(url));
        } catch (Exception e) {
            log.error("getArticle error: " + e.getMessage());
            return new ResponseData();
        }
    }

    @ApiOperation("根据Url获取图片")
    @GetMapping("/image")
    @ResponseBody
    public void getImg(@RequestParam(value = "url") @NotNull String url, HttpServletResponse response) {
        try {
            String fmt = "jpg";
            String[] querys = URLUtils.getURL(url).getQuery().split("&");
            for (String query : querys) {
                if (query.startsWith("wx_fmt=")) {
                    fmt = query.replace("wx_fmt=", "");
                    break;
                }
            }
            if (!"jpg".equals(fmt) || !"jpeg".equals(fmt) || !"png".equals(fmt) || !"gif".equals(fmt)) {
                fmt = "jpg";
            }
            DataOutputStream dout = new DataOutputStream(response.getOutputStream());
            BufferedImage image = ImageIO.read(new URL(url));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(image, fmt, out);
            byte[] b = out.toByteArray();
            dout.write(b);
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @ApiOperation("根据Url获取图片")
    @GetMapping("/image/gif")
    @ResponseBody
    public void getGif(@RequestParam(value = "url") @NotNull String url, HttpServletResponse response) {
        try {
            DataOutputStream dout = new DataOutputStream(response.getOutputStream());
            BufferedImage image = ImageIO.read(new URL(url));
            Graphics2D g = image.createGraphics();
            try {
                // Here's the trick, with DstOver we'll paint "behind" the original image
                g.setComposite(AlphaComposite.DstOver);
                g.setColor(Color.GRAY);
                g.fill(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
            } finally {
                g.dispose();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(image, "GIF", out);
            byte[] b = out.toByteArray();
            dout.write(b);
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @ApiOperation("根据Url获取图片")
    @GetMapping("/image/gif2")
    @ResponseBody
    public void getGif2(@RequestParam(value = "url") @NotNull String url, HttpServletResponse response) {
        try {
            File file = new File("/opt/weixin/original.gif");
            BufferedImage image = ImageIO.read(file);

            // TODO: Test if image has transparency before doing anything else,
            // otherwise just copy the original as-is, for even better performance

            Graphics2D g = image.createGraphics();

            try {
                // Here's the trick, with DstOver we'll paint "behind" the original image
                g.setComposite(AlphaComposite.DstOver);
                g.setColor(Color.GRAY);
                g.fill(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
            } finally {
                g.dispose();
            }

            File out = new File(file.getParent() + File.separator + file.getName().replace('.', '_') + "_out.gif");
            ImageIO.write(image, "GIF", out);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
