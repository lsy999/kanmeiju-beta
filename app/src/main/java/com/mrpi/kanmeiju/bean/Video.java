package com.mrpi.kanmeiju.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mrpi on 2016/6/26.
 */
public class Video implements Serializable {

    private int resultCode;
    private String resultString;

    public List<Meiju> getResultContent() {
        return resultContent;
    }

    public void setResultContent(List<Meiju> resultContent) {
        this.resultContent = resultContent;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public List<Meiju> resultContent;

    public class Meiju {
        private int videoId;
        private String infoUrl;
        private String description;
        private String picUrl;
        private int page;
        private int pageCount;

        public int getVideoId() {
            return videoId;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getPage() {
            return page;
        }

        public String getInfoUrl() {
            return infoUrl;
        }

        public void setInfoUrl(String infoUrl) {
            this.infoUrl = infoUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }

}
