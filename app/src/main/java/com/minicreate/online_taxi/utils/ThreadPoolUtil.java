package com.minicreate.online_taxi.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {

    private ExecutorService mExecutorService;
    private static ThreadPoolUtil instance = new ThreadPoolUtil();

    private ThreadPoolUtil() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static ThreadPoolUtil get() {
        return instance;
    }

    public ExecutorService getThread() {
        return mExecutorService;
    }

}
