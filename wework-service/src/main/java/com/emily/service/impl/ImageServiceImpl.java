package com.emily.service.impl;

import com.alice.emily.utils.LOG;
import com.alice.emily.utils.logging.Logger;
import com.emily.constant.Constants;
import com.emily.params.ThumbParameter;
import com.emily.service.ImageService;
import com.emily.utils.Base64Utils;
import com.google.common.base.Preconditions;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Lianhao on 2017/9/22.
 */
@Service
public class ImageServiceImpl implements ImageService {
    private final Logger log = LOG.getLogger(ImageService.class);

    @Override
    @Cacheable(value = "getThumbByPath")
    public byte[] getThumbByPath(ThumbParameter parameter) throws Exception {
        //Base64解密
        String path = Base64Utils.base64Decode(parameter.getPath());
        File file = new File(path);
        Preconditions.checkArgument(file.exists(), path + "file is not exit");
        Preconditions.checkArgument(path.matches(Constants.IMAGE_PATTERN), path + " is not accept");

        byte[] data;
        if (null != parameter.getWidth() && null != parameter.getHeight()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Thumbnails.of(path)
                    .size(parameter.getWidth(), parameter.getHeight())
                    .outputFormat("png")
                    .toOutputStream(os);
            os.flush();
            data = os.toByteArray();
            os.close();
        } else {
            File thumbFile = new File(path);
            FileInputStream inputStream = new FileInputStream(thumbFile);
            data = new byte[(int) thumbFile.length()];
            inputStream.read(data);
            inputStream.close();
        }
        return data;
    }
}
