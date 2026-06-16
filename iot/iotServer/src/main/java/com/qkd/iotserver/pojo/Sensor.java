package com.qkd.iotserver.pojo;

import lombok.Data;

import java.text.SimpleDateFormat;

@Data
public class Sensor {
    public String 光照强度;
    public String 土壤情况;
    public String 大棚室温;
    public String 空气湿度;
    public String 大棚顶棚;
    public String 水肥机;
    public String 补光灯;
    public String 排风机;
    public String 自动手动;
    public String 更新时间;
    public String devip; // 用于比较是哪个大棚

    // 数据解析
    public static Sensor parse(String data) {
        Sensor sensor = new Sensor();
        String[] myData = data.split("-");
        sensor.光照强度 = myData[1];
        sensor.土壤情况 = myData[2];
        String temp = myData[3].substring(1, myData[3].length() - 1);
        sensor.大棚室温 = temp.split(",")[0];
        sensor.空气湿度 = temp.split(",")[1];
        String ctrl = myData[4].substring(1, myData[4].length() - 1);
        sensor.大棚顶棚 = ctrl.split(",")[0];
        sensor.水肥机 = ctrl.split(",")[1];
        sensor.补光灯 = ctrl.split(",")[2];
        sensor.排风机 = ctrl.split(",")[3];
        sensor.自动手动 = myData[5];
        long timestamp = Long.parseLong(myData[6]);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sensor.更新时间 = sdf.format(timestamp);
        sensor.devip = myData[7];
        return sensor;
    }

}
