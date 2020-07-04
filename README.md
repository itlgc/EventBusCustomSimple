### 自定义事件总线框架

参考EventBus实现原理，手写一个简易的事件总线框架，学习实现思路。



#### 线程状态枚举类

用于@Subscribe注解中的处理线程

●默认为POSTING
●主线程-主线程，子线程-子线程

```java
public enum ThreadMode {
    //事件的处理在和事件的发送在相同的进程
    POSTING ,
    //事件的处理会在UI线程中执行
    MAIN ,
    //后台进程，处理如保存到数据库等操作
    BACKGROUND ,
    //异步执行，另起线程操作。事件处理会在单独的线程中执行主要用于在后台线程中执行耗时操作
    ASYNC
}
```



#### 订阅方法封装类（理解为JavaBean）

```java
public class SubscribleMethod {
    //注册方法
    private Method method;
    //线程类型
    private ThreadMode threadMode;
    //参数类型
    private Class<?> eventType;
  
  	//省略构造  set/get方法....
}
```





#### 收集Activity或Fragment所有的订阅方法

```java
/**
 * 获取订阅者中所有的 订阅方法（即注解方法）
 * @param subscriber 订阅者 MainActivity
 * @return
 */
private List<SubscribleMethod> findSubscribeMethod(Object subscriber) {
    List<SubscribleMethod> list = new ArrayList<>();
    Class<?> aClass = subscriber.getClass();
    Method[] methods = aClass.getMethods();//获取包括父类在内的所有方法
		//省略部分代码.....

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

      //将符合规范的方法 保存到MethodManager中   (3个重要成员：方法、参数、线程)
      SubscribleMethod methodManager = new SubscribleMethod(method, annotation.threadMode(),
                                                            parameterTypes[0]);
      list.add(methodManager);
    }

      
    return list;
}
```





#### 发送事件

```java
public void post(final Object setter) {
    Set<Object> objects = cacheMap.keySet();
    for (final Object subscriber : objects) {
        //获取到所有注解方法
        List<SubscribleMethod> methodList = cacheMap.get(subscriber);
        if (methodList != null) {
            for (final SubscribleMethod method : methodList) {
                //判断这个方法是否应该接收事件
                if (method.getEventType().isAssignableFrom(setter.getClass())) {
                  //线程调度
                    switch (method.getThreadMode()) {
                        case POSTING:
                            //.....
                            break;
                        case MAIN://如果接收方法在主线程执行的情况
                            //.....
                            break;

                        case BACKGROUND: //接收方法在子线程的情况
                            //.....
                            break;

                        default:

                            break;
                    }
                }
            }
        }
    }
}
```



