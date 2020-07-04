package com.it.eventbus_lib.mode;

/**
 * Created by lgc on 2020-02-23.
 */
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
