package com.qkd.iotserver.controller;

import com.qkd.iotserver.controller.HttpController;
import com.qkd.iotserver.mapper.DeviceMapper;
import com.qkd.iotserver.mapper.ProductMapper;
import com.qkd.iotserver.pojo.Device;
import com.qkd.iotserver.pojo.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/device")
public class DeviceController extends HttpController<DeviceMapper, Device> {
    @Autowired
    DeviceMapper deviceMapper;
    @Autowired
    ProductMapper productMapper;

    @RequestMapping("/device")
    public List<Device> device(int pid){
        System.out.println("产品id为" + pid);
        return this.pin(deviceMapper.provdev(pid));
    }

    private List<Device> pin(List<Device> list) {
        list.forEach(item->item.category=item.category+" ("+item.pin+")");
        return list;
    }

    @RequestMapping("/pid")
    public Record pid(Integer uid, String key) {
        final Record record = new Record();
        if(uid ==null) {
            record.putAll(super.menu(key));
        } else {
            productMapper.mypro(uid).forEach(item -> {
                record.add("product", item.product, item.id);
            });
        }
        return record;
    }

    @RequestMapping("/dev")
    public List<Device> dev(int uid){
        return this.pin(deviceMapper.mydev(uid));
    }

    @Override
    public List<Device> select() {
        return this.pin(super.select());
    }

    @Override
    public Object insert(@RequestBody Device bean) {
        bean.setIdcard();
        return super.insert(bean);
    }
}
