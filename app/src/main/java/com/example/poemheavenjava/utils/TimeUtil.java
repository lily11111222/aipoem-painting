package com.example.poemheavenjava.utils;

public class TimeUtil {
    //开始时间类，几分几秒
    private Integer minute = 0;
    private Integer second = 0;

    public TimeUtil(Integer minute, Integer second){
        this.minute = minute;
        this.second = second;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }
}
