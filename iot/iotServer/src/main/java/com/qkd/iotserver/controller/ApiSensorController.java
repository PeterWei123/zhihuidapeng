package com.qkd.iotserver.controller;


import com.google.gson.Gson;
import com.qkd.iotserver.mapper.DatadictMapper;
import com.qkd.iotserver.mapper.ProductMapper;
import com.qkd.iotserver.pojo.Datadict;
import com.qkd.iotserver.pojo.Sensor;
import com.qkd.iotserver.util.EventBus;
import com.qkd.iotserver.util.HttpConn;
import com.qkd.iotserver.util.WSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiSensorController {
    @Autowired
    DatadictMapper datadictMapper;
    @Autowired
    ProductMapper productMapper;

    @RequestMapping("/sensor")
    public void sensor(String data) {
        Sensor sensor = Sensor.parse(data); // 解析data，解析成sensor对象

        // 如何获取产品名称？
        String devip = sensor.devip.substring(0, sensor.devip.length()-5);
        String mykey = null;
        try { // 匹配上了，就用产品名称当做mykey
            mykey = productMapper.getLike(devip).product;
        } catch (Exception e) {
            mykey = devip;
        }
        String json = new Gson().toJson(sensor);
        if (datadictMapper.getValue(mykey) == null) {
            // tbl_datadict表中没有，则完成新增操作
            datadictMapper.insert(new Datadict(mykey,json));
        } else { // 表中有这个设备的传感器数据，每隔10s做一次更新
            datadictMapper.setValue(new Datadict(mykey,json));
        }
        EventBus.getDefault().postUpdate(json);
    }

    @RequestMapping("/javaip")
    public String javaip(String devip) {
        // 前端把设备ip传过来了
        System.out.println(devip); // 前端传
        String javaip = WSUtil.localHttpIP();
        System.out.println(javaip);
        try { // java访问python
            HttpConn.get(devip + "/api/javaip?javaip=" + javaip);
            return "成功";
        } catch (Exception e) {
            return "1.网断了 2.设备IP写错了 3.Python程序/设备启动失败";
        }

    }
}
