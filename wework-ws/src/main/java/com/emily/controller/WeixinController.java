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
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            if (URLUtils.getURL(url).getQuery()!=null) {
                String[] querys = URLUtils.getURL(url).getQuery().split("&");
                for (String query : querys) {
                    if (query.startsWith("wx_fmt=")) {
                        fmt = query.replace("wx_fmt=", "");
                        break;
                    }
                }
            } else {
                String[] querys = url.split("/");
                if(querys[querys.length-1].contains(".")){
                    String query = querys[querys.length-1];
                    String[] fmts = query.split("\\.");
                    fmt = fmts[1];
                }
            }

            if (!"jpg".equals(fmt) && !"png".equals(fmt) && !"gif".equals(fmt)) {
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

    @GetMapping("/image/gif")
    @ApiIgnore
    @ResponseBody
    public void getGif(@RequestParam(value = "url") @NotNull String url, HttpServletResponse response) {

        byte[] imageByteArray = null;
        try {
            imageByteArray = createByteArray(url);
            DataOutputStream dout = new DataOutputStream(response.getOutputStream());
            InputStream in = new ByteArrayInputStream(imageByteArray);
            BufferedImage image = ImageIO.read(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean flag = ImageIO.write(image, "GIF", out);
            if (!flag) {
                System.out.println("Could not save as gif, image had too many colors");
            }
            byte[] b = out.toByteArray();
            dout.write(b);
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @GetMapping("/image/gif2")
    @ApiIgnore
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

    public void saveAnimatedGifFramePartsToImage(String input, String outDir) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        try (ImageInputStream ciis = ImageIO.createImageInputStream(new File(input))) {
            reader.setInput(ciis, false);
            for (int i = 0, noi = reader.getNumImages(true); i < noi; i++) {
                BufferedImage image = reader.read(i);
                ImageIO.write(image, "GIF", new File(new File(outDir), i + ".gif"));
            }
        }
    }

    // Constraint:  This method simulates how the image is originally received
    private byte[] createByteArray(String urlString) throws IOException {
        URL url = new URL(urlString);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url.openStream();
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return baos.toByteArray();
    }

}
