package com.sunruncn.lib.mqtt;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/8/24.
 */
public class TimeClock implements Serializable {

    private byte id;    //周期的字节表示
    private byte timeId;    //定时器id
    private int state;  //开关状态 0代表关 1代表开
    private int hour;   //定时的小时数
    private int minute; //定时的分钟数
    private String period;  //周期
    private int mWay;  //线路
    private ArrayList<Integer> mWays = new ArrayList<>();    //要操作的线路

    public TimeClock() {
    }

    public TimeClock(byte id, int state, int hour, int minute, String period) {
        this.id = id;
        this.state = state;
        this.hour = hour;
        this.minute = minute;
        this.period = period;
    }

    public byte getId() {
        return id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public byte getTimeId() {
        return timeId;
    }

    public void setTimeId(byte timeId) {
        this.timeId = timeId;
    }

    public int getWay() {
        return mWay;
    }

    public void setWay(int way) {
        mWay = way;
    }

    public ArrayList<Integer> getWays() {
        return mWays;
    }

    public void setWays(ArrayList<Integer> ways) {
        mWays = ways;
    }
}
