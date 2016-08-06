package com.mrpi.kanmeiju.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrpi on 2016/6/26.
 */
public class Type {

    public static String TYPE_HOME = "http://kanmeiju.net";
    public static String TYPE_KH = "http://kanmeiju.net/vodlist/3.html";
    public static String TYPE_MOVIE = "http://kanmeiju.net/vodlist/24.html";
    public static String TYPE_XJ = "http://kanmeiju.net/vodlist/22.html";
    public static String TYPE_XY = "http://kanmeiju.net/vodlist/5.html";
    public static List<String> TypeNames = new ArrayList<>();
    public static List<String> TypeUrls = new ArrayList<>();
    public static int TYPE_XY_CODE = 1;
    public static int TYPE_KH_CODE = 2;
    public static int TYPE_XJ_CODE = 3;
    public static int TYPE_MV_CODE = 4;

    public static List<String> getTypeUrls() {
        TYPE_HOME = "http://kanmeiju.net";
        TYPE_XY = "http://kanmeiju.net/vodlist/5.html"; //悬疑
        TYPE_KH = "http://kanmeiju.net/vodlist/3.html"; //科幻
        TYPE_XJ = "http://kanmeiju.net/vodlist/22.html"; //喜剧
        TYPE_MOVIE = "http://kanmeiju.net/vodlist/24.html";
        TypeUrls.add(TYPE_HOME);
        TypeUrls.add(TYPE_XY);
        TypeUrls.add(TYPE_KH);
        TypeUrls.add(TYPE_XJ);
        TypeUrls.add(TYPE_MOVIE);
        return TypeUrls;
    }

    public static void setTypeUrls(List<String> typeUrls) {
        TypeUrls = typeUrls;
    }

    public static List<String> getTypeNames() {
        return TypeNames;
    }

    public static void setTypeNames(List<String> typeNames) {
        Type.TypeNames = typeNames;
    }
}
