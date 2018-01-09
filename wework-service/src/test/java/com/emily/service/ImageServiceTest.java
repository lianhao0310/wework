package com.emily.service;

import com.emily.WeworkServiceApplicationTests;
import com.emily.constant.Constants;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Created by Lianhao on 2017/11/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeworkServiceApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ImageServiceTest {

    @Test
    public void getByeByUrl() throws Exception {
//        byte[] data;
//        String url = "http://img01.sogoucdn.com/net/a/04/link?appid=100520033&url=http%3A%2F%2Fmmbiz.qpic.cn%2Fmmbiz_jpg%2FibQ2cXpBDzUOjhrNibyhzVbazlBicrwfW3u6Wr3P4FAquricU9enDiawfdnQmRWzRvibzwyzZ3V6z4dSj6q6DMqI5YBw%2F0%3Fwx_fmt%3Djpeg";
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        Thumbnails.of(new URL(url))
//                .size(100, 100)
//                .outputFormat("png")
//                .toOutputStream(os);
//        os.flush();
//        data = os.toByteArray();
//        os.close();
//        System.out.println(data);
        String url = "https://storage.googleapis.com/google-code-attachments/thumbnailator/issue-65/comment-1/1.gif";
        Thumbnails.of("/opt/weixin/original.gif")
                .size(80, 80)
                .outputQuality(1f)
//                .imageType(BufferedImage.TYPE_INT_ARGB)
                .toFile("/opt/weixin/after.gif");
    }


    @Test
    public void testResized() throws Exception {
        String url = "https://storage.googleapis.com/google-code-attachments/thumbnailator/issue-65/comment-1/1.gif";
        BufferedImage bufferedImage = ImageIO.read(new URL(url));
//        ImageIO.write(bufferedImage , "JPEG", response.getOutputStream());
    }

    public BufferedImage createResizedCopy(Image originalImage,
                                           int scaledWidth, int scaledHeight,
                                           boolean preserveAlpha) {
        System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }

    @Test
    public void testFmt() {
        String fmt = "image.jpg";
        System.out.println(fmt.matches(Constants.IMAGE_PATTERN));
    }
}