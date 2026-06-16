package com.qkd.iotserver.util;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    List<Observer> observerList = new ArrayList<>();
    public int length = 0, index = 0;
    public static EventBus bus = null;
    //讲一下synchronized
    public synchronized static EventBus getDefault() {
        if (bus == null) {
            bus = new EventBus();
        }
        return bus;
    }

    public void register(Observer observer) {
        observerList.add(observer);
        length = observerList.size();
    }
    public void unregister(Observer observer) {
        observerList.remove(observer);
        length = observerList.size();
        index--;
    }
    public void postUpdate(String msg) {
        for (index = 0; index < length; index++) {
            observerList.get(index).update(msg);
        }
    }
}
