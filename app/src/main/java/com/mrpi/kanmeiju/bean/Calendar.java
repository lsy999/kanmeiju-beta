package com.mrpi.kanmeiju.bean;

import java.util.List;

/**
 * Created by acer on 2016/7/14.
 */
public class Calendar {


    private int resultCode;
    private String resultString;
    public List<resultContent> resultContent;


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

    public List<Calendar.resultContent> getResultContent() {
        return resultContent;
    }

    public void setResultContent(List<Calendar.resultContent> resultContent) {
        this.resultContent = resultContent;
    }

    public class resultContent{
        private String station;
        private String name;
        private String time;
        private String pic;
        private String size;
        private int week;

        public String getStation() {
            return station;
        }

        public void setStation(String station) {
            this.station = station;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }


    }
}
