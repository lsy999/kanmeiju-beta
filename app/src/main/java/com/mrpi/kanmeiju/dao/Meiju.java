package com.mrpi.kanmeiju.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MEIJU".
 */
public class Meiju {

    private Long id;
    private String videoId;
    /** Not-null value. */
    private String name;
    private String desc;
    private Boolean follow;
    private String Episode;
    private String picture;
    private java.util.Date date;
    private String url;
    private Boolean hasUpdate;
    private Integer click;
    private String local;

    public Meiju() {
    }

    public Meiju(Long id) {
        this.id = id;
    }

    public Meiju(Long id, String videoId, String name, String desc, Boolean follow, String Episode, String picture, java.util.Date date, String url, Boolean hasUpdate, Integer click, String local) {
        this.id = id;
        this.videoId = videoId;
        this.name = name;
        this.desc = desc;
        this.follow = follow;
        this.Episode = Episode;
        this.picture = picture;
        this.date = date;
        this.url = url;
        this.hasUpdate = hasUpdate;
        this.click = click;
        this.local = local;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public String getEpisode() {
        return Episode;
    }

    public void setEpisode(String Episode) {
        this.Episode = Episode;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(Boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public Integer getClick() {
        return click;
    }

    public void setClick(Integer click) {
        this.click = click;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

}
