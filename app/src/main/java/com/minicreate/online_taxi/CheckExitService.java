package com.minicreate.online_taxi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.minicreate.online_taxi.config.EndpointConfig;
import com.minicreate.online_taxi.transmission.BusinessFlowManager;
import com.minicreate.online_taxi.transmission.protocol.beidou.Logout;
import com.minicreate.online_taxi.utils.LogUtil;

public class CheckExitService extends Service {
    private static final String TAG = "CheckExitService";

    public CheckExitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.d(TAG, "onTaskRemoved rootIntent = " + rootIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int re = super.onStartCommand(intent, flags, startId);
        LogUtil.d(TAG, "onStartCommand intent = " + intent + " ,re = " + re);
        return re;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
//        Logout logout = new Logout();
//        BusinessFlowManager.get(EndpointConfig.SERVER_TEST.getIp(), getApplicationContext()).sendSetupCommand(null, null, null, 0);
        super.onDestroy();
    }
}
