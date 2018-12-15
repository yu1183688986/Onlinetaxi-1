package com.minicreate.online_taxi.transmission;

import android.content.Context;

import com.minicreate.online_taxi.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务流程管理类，使用了享元模式
 */
public class BusinessFlowManager {
    private static final String TAG = "BusinessFlowManager";
    private static Map<String, BusinessFlow> businessFlowMap = new HashMap<>();

    public static BusinessFlow get(String name, Context context) {
        BusinessFlow businessFlow = businessFlowMap.get(name);

        if (businessFlow == null) {
            LogUtil.e(TAG, "BusinessFlow[" + name + "] is null");
            //重建BusinessFlow
            businessFlow = new BusinessFlow();
            businessFlow.init(name, context);
            businessFlowMap.put(name, businessFlow);
            LogUtil.e(TAG, "BusinessFlow[" + name + "] created");
        }
        return businessFlow;
    }

    public static BusinessFlow remove(String name) {
        return businessFlowMap.remove(name);
    }
}
