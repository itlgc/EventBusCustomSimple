package com.it.eventbus_lib;

import android.os.Handler;
import android.os.Looper;
import com.it.eventbus_lib.annotation.Subscribe;
import com.it.eventbus_lib.core.SubscribleMethod;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lgc on 2020-02-23.
 */
public class EventBus {

    private static EventBus instance;

    private Handler handler;
    private ExecutorService executorService;
    private Map<Object, List<SubscribleMethod>> cacheMap;

    private EventBus() {
        cacheMap = new HashMap<>();
        //将Handler放在主线程使用
        handler = new Handler(Looper.getMainLooper());
        //创建一个子线程  （缓存线程池）
        executorService = Executors.newCachedThreadPool();
    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    //注册
    public void register(Object subscriber) {
        List<SubscribleMethod> subscribeMethodList = cacheMap.get(subscriber);
        if (subscribeMethodList == null) { //不为空表示之前完成了注册
            subscribeMethodList = findSubscribeMethod(subscriber);
            cacheMap.put(subscriber, subscribeMethodList);
        }

    }

    /**
     * 获取订阅者中所有的 订阅方法（即注解方法）
     * @param subscriber 订阅者 MainActivity
     * @return
     */
    private List<SubscribleMethod> findSubscribeMethod(Object subscriber) {
        List<SubscribleMethod> list = new ArrayList<>();
        Class<?> aClass = subscriber.getClass();

        Method[] methods = aClass.getMethods();//获取包括父类在内的所有方法

        while (aClass != null) {
            //如果是系统类 不添加到cacheMap
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }


            for (Method method : methods) {
                //获取方法注解
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation == null) {
                    continue;
                }

                //控制方法格式和规范
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException(method.getName() + "有且只能有一个参数");
                }

                //将符合规范的方法 保存到MethodManager 中
                SubscribleMethod methodManager = new SubscribleMethod(method, annotation.threadMode(),
                        parameterTypes[0]);
                list.add(methodManager);
            }

            //不断循环找出父类含有订阅注解方法的类
            aClass = aClass.getSuperclass();

        }
        return list;
    }



    //取消注册
    public void unregister(Object subscriber) {
        List<SubscribleMethod> list = cacheMap.get(subscriber);
        //如果获取到
        if (list != null) {
            cacheMap.remove(subscriber);
        }
    }

    //发送消息
    public void post(final Object setter) {
        Set<Object> objects = cacheMap.keySet();
        for (final Object subscriber : objects) {
            //获取到所有注解方法
            List<SubscribleMethod> methodList = cacheMap.get(subscriber);
            if (methodList != null) {
                for (final SubscribleMethod method : methodList) {

                    //判断这个方法是否应该接收事件
                    if (method.getEventType().isAssignableFrom(setter.getClass())) { //方法参数类型比较

                        switch (method.getThreadMode()) {
                            case POSTING:
                                invoke(method,subscriber,setter);
                                break;

                            case MAIN://如果接收方法在主线程执行的情况
                                if(Looper.myLooper() == Looper.getMainLooper()){
                                    invoke(method,subscriber,setter);
                                } else {
                                    //切换线程  子线程——> 主线程 （使用handler）
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            invoke(method,subscriber,setter);
                                        }
                                    });
                                }
                                break;

                            case BACKGROUND: //接收方法在子线程的情况
                                if(Looper.myLooper() == Looper.getMainLooper()){
                                    //切换线程  主线程——> 子线程
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            invoke(method,subscriber,setter);
                                        }
                                    });
                                } else {
                                    invoke(method,subscriber,setter);
                                }
                                break;

                            default:

                                break;
                        }
                    }
                }
            }

        }
    }


    //找到方法后通过反射执行
    private void invoke(SubscribleMethod subscribleMethod, Object subscriberObj, Object setter) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(subscriberObj, setter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
