package com.qkd.iotserver.util;

import javax.websocket.Session;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WSUtil {

    // websocket发送消息
    public static void sendClient(Session session, String json) {
        try {
            session.getBasicRemote().sendText(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String ip() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress ip = socket.getLocalAddress();
            return ip.getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    public static int port() {
        return 8080;
    }

    public static String localHttpIP() {
        return "http://" + ip() + ":" + port();
    }

}