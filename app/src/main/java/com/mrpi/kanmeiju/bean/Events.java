package com.mrpi.kanmeiju.bean;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by acer on 2016/7/13.
 */
public class Events {

    public static final int VIDEO_FOLLOW = 1001;
    public static final int VIDEO_UPDATE = 1002;
    public static final int SERVICE_DESTROY = 1003;
    public static final int SERVICE_START = 1004;
    public static final int BANNER_SHOW = 1005;
    public static final int BANNER_HIDE = 1006;

    private int code;
    private Object tag;

    public Events(int code){
        this.code = code;
    }

    public Events(int code, Object tag) {
        this.code = code;
        this.tag = tag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public static void postVideoFollow(@Nullable Object tag){
        EventBus.getDefault().post(new Events(VIDEO_FOLLOW, tag));
    }

    public static void postVideoUpdate(@Nullable Object tag){
        EventBus.getDefault().post(new Events(VIDEO_UPDATE,tag));
    }

    public static void postServiceDestroy(){
        EventBus.getDefault().post(new Events(SERVICE_DESTROY));
    }
    public static void postServiceStart(){
        EventBus.getDefault().post(new Events(SERVICE_START));
    }

    public static void postBannerShow(){
        EventBus.getDefault().post(new Events(BANNER_SHOW));
    }
    public static void postBannerHide(){
        EventBus.getDefault().post(new Events(BANNER_HIDE));
    }
}
