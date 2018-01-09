package com.emily.utils;

/**
 * Created by emily on 2018/1/8.
 */
public class UrlUtils {

    public static String getUrlQuery(String url, String query) {
        String[] urlStrs = url.split("[?]");
        String[] urlQurys = urlStrs[1].split("&");
        for (String urlQuery : urlQurys) {
            String[] urlQe = urlQuery.split("=");
            if (query.equals(urlQe[0])) {
                String vid = urlQe[1];
                return vid;
            }
        }
        return null;
    }
}
