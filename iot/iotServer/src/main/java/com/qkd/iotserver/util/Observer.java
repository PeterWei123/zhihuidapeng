package com.qkd.iotserver.util;

public interface Observer {
    void update(String msg);
    void destroy();
}
