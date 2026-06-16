package com.qkd.iotserver.controller;

import com.qkd.iotserver.util.EventBus;
import com.qkd.iotserver.util.Observer;
import com.qkd.iotserver.util.WSUtil;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

@Component
@ServerEndpoint("/websocket")
public class ApiSensorWebSockt implements Observer {
    List<Session> sessionList = new ArrayList<>(); //
    // --------不重要--------
    @OnOpen
    public void open(Session session) {
        System.out.println("打开一个连接，连上客户端了");
        sessionList.add(session);
    }
    @OnClose
    public void close(Session session) {
        System.out.println("断开一个连接，与客户端断开");
        sessionList.remove(session);
    }
    // ---------------------
    @OnMessage
    public void message(Session session, String message) throws Exception {
        System.out.println("客户端/浏览器，发来了消息");
    }

    // 1.new ApiSensorWebSockt() 2.让Spring去new
    public ApiSensorWebSockt() { // 程序启动时，把观察者加入到队列中
        EventBus.getDefault().register(this);
    }

    @Override
    public void update(String msg) { // 接收消息
        System.out.println(msg);
        for (Session session : sessionList) {
            WSUtil.sendClient(session, msg);
        }
    }

    @Override // 在程序停的时候调用
    public void destroy() { // 从观察者队列中退出，不再是观察者了
        EventBus.getDefault().unregister(this);
    }
}