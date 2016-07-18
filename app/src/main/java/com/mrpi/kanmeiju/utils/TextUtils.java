package com.mrpi.kanmeiju.utils;

import android.net.Uri;

/**
 * Created by acer on 2016/7/15.
 */
public class TextUtils {
    public static String getUrl(String url){
        if(url.startsWith("http"))
            return url;
        else
            return "http://kanmeiju.net"+ url;

    }
}
