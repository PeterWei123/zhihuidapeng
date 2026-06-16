package com.qkd.iotserver.controller;

import com.qkd.iotserver.mapper.UserMapper;
import com.qkd.iotserver.pojo.Record;
import com.qkd.iotserver.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@CrossOrigin//允许前端请求后端
@RestController//
@RequestMapping("/user")//第一段请求
public class UserController extends HttpController<UserMapper,User>{
    @Autowired//
    UserMapper userMapper;

    @RequestMapping("/option")
    public Record option(){
        Record record = new Record();
        record.opt("status","租户","管理员");
        record.opt("state","未审核","审核成功");
        return record;
    }

    @RequestMapping("/login")//第二段请求
    public Map<String,Object> login(User user) {
        System.out.println(user.username);
        System.out.println(user.password);
        Map<String,Object> data = new HashMap<>();
        User result =userMapper.getUser(user);
        if (result != null) {
            data.put("code",1);
            data.put("uid",result.id);
            data.put("status",result.status);
        } else {
            data.put("code",0);

        }
        return data;

    }
}