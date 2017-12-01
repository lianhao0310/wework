package com.emily.utils;

import com.alice.emily.utils.LOG;
import com.alice.emily.utils.logging.Logger;
import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Lianhao on 2017/9/25.
 */
public class Base64Utils {

    private static final Logger logger = LOG.getLogger(Base64Utils.class);

    private static final String UTF_8 = "UTF-8";


    public static String base64Encode(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.encodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error(inputData, e);
        }

        return null;
    }

    public static String base64Decode(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.decodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error(inputData, e);
        }

        return null;
    }

    public static String getCommonPicture(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return "";
        }
        return "http://api.mallinsight.ipalmap.com/image?path=" + base64Encode(path);
    }

    public static List<String> getCommonPictures(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            return paths.stream().map(Base64Utils::getCommonPicture).collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public static String getHeader(String key, String secret) {
        String auth = key + ":" + secret;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }
}
