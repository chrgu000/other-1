package com.anosi.asset.bean;

import java.io.Serializable;

/**
* 用于接受数据中心传感器采集信息
* @since  2018/7/13 16:34
* @author 倪文骅
* @param null
* @return
*/
public class DateMonitor implements Serializable {

    private String dustMac;
    private String temperature;
    private String humidity;
    private String collectTime;

    public String getDustMac() {
        return dustMac;
    }

    public void setDustMac(String dustMac) {
        this.dustMac = dustMac;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
}
