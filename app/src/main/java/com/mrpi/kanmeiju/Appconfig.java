package com.mrpi.kanmeiju;

/**
 * Created by mrpi on 2016/7/4.
 */
public class Appconfig {
    public static final String URL_GET = "http://45.78.14.98:8080/kanmeiju/meiju/get?"; //page=1&type=1
    public static final String URL_DT = "http://45.78.14.98:8080/kanmeiju/meiju/detail?id="; //page=1&type=1
    public static final String URL_SH = "http://45.78.14.98:8080/kanmeiju/meiju/search?"; //page=1&type=1
    public static final String URL_TIME = "http://45.78.14.98:8080/kanmeiju/meiju/time";//更新时间表
    public static final String URL_HOT = "http://45.78.14.98:8080/kanmeiju/meiju/hot";
    public static final int ACTION_LOAD_INIT = 1;
    public static final int ACTION_LOAD_MORE = 2;
    public static final int TYPE_XY = 1;
    public static final int TYPE_KH = 2;
    public static final int TYPE_XJ = 3;
    public static final int TYPE_MV = 4;
}
