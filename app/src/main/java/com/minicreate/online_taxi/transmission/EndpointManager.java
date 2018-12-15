package com.minicreate.online_taxi.transmission;

import android.util.Log;

import com.minicreate.online_taxi.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Clock
 * @version V1.0
 * @ClassName: EndpointManager.java
 * @Date 2014-9-19 上午11:53:13
 * @Description:存放端点的管理类。 TODO 会不会有可能，因为EndpointManager的引用没有被其他类持有，如果长时间没有进行网络传输，导致EndpointManager被系统回收了，结果其他地方就找不到网络端点了。
 * TODO 使用心跳包就能很好地解决这个问题，发送和接收的心跳包都不要隔太久，这个类就能被反复调用。
 */
public class EndpointManager {
    private EndpointManager() {
    }

    private static EndpointManager instance = new EndpointManager();
    private Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();

    public static EndpointManager get() {
        return instance;
    }

    public Endpoint getEndpointById(String strId) {
        d("查找通信端点 " + strId);
        synchronized (endpoints) {
            if (endpoints.containsKey(strId)) {
                d("找到通信端点... " + strId);
                return endpoints.get(strId);
            } else {
                d("------> 没找到通信端点！！！！！！！！！ " + strId);
                return null;
            }
        }
    }

    public boolean exist(String id) {
        return endpoints.containsKey(id);
    }

    /**
     * 因为端点会重建，所以如果列表中已存在端点，那么就更新该端点
     *
     * @param e
     */
    public void addEndpoint(Endpoint e) {
        synchronized (endpoints) {
            if (!endpoints.containsKey(e.getName())) {
                d("添加新端点 " + e.getName());
            } else {
                d("已经存在此端点 " + e.getName() + " ,更新端点");
            }
            endpoints.put(e.getName(), e);
        }
    }

    public void removeEndpoint(Endpoint e) {
        synchronized (endpoints) {
            if (endpoints.containsKey(e.getName())) {
                endpoints.remove(e.getName());
                d("删除一个通信端点 " + e.getName());
            } else {
                d("端点未找着 " + e.getName());
            }
        }
    }

    public void removeAllEndpoint() {
        synchronized (endpoints) {
            endpoints.clear();
        }
    }

    void d(String msg) {
        final String TAG = "EndpointManager";
        LogUtil.w(TAG, msg);
    }
}
