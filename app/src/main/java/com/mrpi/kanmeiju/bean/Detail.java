package com.mrpi.kanmeiju.bean;

import java.util.List;

/**
 * Created by mrpi on 2016/6/27.
 */
public class Detail {
    private int resultCode;
    private String resultString;
    public resultContent resultContent;


    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public Detail.resultContent getResultContent() {
        return resultContent;
    }

    public void setResultContent(Detail.resultContent resultContent) {
        this.resultContent = resultContent;
    }

    public class resultContent{
        private String name;
        private String detail;
        private List<String> info;
        private List<String> ed2k;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public List<String> getInfo() {
            return info;
        }

        public void setInfo(List<String> info) {
            this.info = info;
        }

        public List<String> getEd2k() {
            return ed2k;
        }

        public void setEd2k(List<String> ed2k) {
            this.ed2k = ed2k;
        }
    }

}
