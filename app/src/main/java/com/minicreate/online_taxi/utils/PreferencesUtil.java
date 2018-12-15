package com.minicreate.online_taxi.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的工具类
 * TODO 搁置，尚未完成
 */
public class PreferencesUtil {
    public static String NAME = "pre_name";

    public static String VehicleNo = "VehicleNo";//车辆编号
    private volatile static PreferencesUtil instance;
    private static SharedPreferences sharedPreferences;

    private PreferencesUtil() {
    }

    public static PreferencesUtil get(Context context, String name) {
        if (instance == null) {
            synchronized (PreferencesUtil.class) {
                if (instance == null) {
                    instance = new PreferencesUtil();
                    init(context, name);
                }
            }
        }
        return instance;
    }

    public static void init(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public synchronized String get(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public synchronized void put(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
