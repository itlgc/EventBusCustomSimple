package com.it.eventbus_lib.core;


import com.it.eventbus_lib.mode.ThreadMode;
import java.lang.reflect.Method;

/**
 * Created by lgc on 2020-02-23.
 *  订阅方法封装类 JavaBean
 */
public class SubscribleMethod {
    //注册方法
    private Method method;
    //线程类型
    private ThreadMode threadMode;
    //参数类型
    private Class<?> eventType;

    public SubscribleMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
