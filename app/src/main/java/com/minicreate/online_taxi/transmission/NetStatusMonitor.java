package com.minicreate.online_taxi.transmission;

import com.minicreate.online_taxi.transmission.listener.NetStatusChangeInterface;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

import java.util.ArrayList;

/**
 * 获取此类实例，便可添加对网络变化的监听
 */
public class NetStatusMonitor {
    private ArrayList<NetStatusChangeInterface> netStatusListener = new ArrayList<NetStatusChangeInterface>();

    private static NetStatusMonitor monitor = new NetStatusMonitor();

    private NetStatusMonitor() {
    }

    public static NetStatusMonitor get() {
        return monitor;
    }

    /**
     * 添加一个网络监听者
     *
     * @param lis
     */
    public void addNetStatusListener(NetStatusChangeInterface lis) {
        synchronized (netStatusListener) {
            if (!netStatusListener.contains(lis)) {
                LogUtil.w("zry", "add: " + lis.getClass());
                netStatusListener.add(lis);
            } else {
                LogUtil.w("zry", "add: 已经存在此类" + lis.getClass().getSimpleName());
            }
        }
    }

    /**
     * 移除一个网络监听者
     *
     * @param lis
     */
    public void removeNetStatusListener(NetStatusChangeInterface lis) {
        synchronized (netStatusListener) {
            if (netStatusListener.contains(lis)) {
                LogUtil.w("zry", "remove: " + lis.getClass());
                netStatusListener.remove(lis);
            } else {
                LogUtil.w("zry", "remove: 此类不存在" + lis.getClass().getSimpleName());
            }
        }
    }

    private void notifyListenerNetOff2On() {
        for (NetStatusChangeInterface lis : netStatusListener) {
            LogUtil.w("zry", "NetOff2On: " + lis.getClass().getSimpleName());
            lis.onNetOff2On();
        }
    }

    private void notifyListenerNetOn2Off() {
        for (NetStatusChangeInterface lis : netStatusListener) {
            LogUtil.w("zry", "On2Off: " + lis.getClass().getSimpleName());
            lis.onNetOn2Off();
        }
    }

    private void notifyListenerNetMoblie2Wifi() {
        for (NetStatusChangeInterface lis : netStatusListener) {
            LogUtil.w("zry", "Moblie2Wifi: " + lis.getClass().getSimpleName());
            lis.onNetMoblie2Wifi();
        }
    }

    private void notifyListenerWifi2Moblie() {
        for (NetStatusChangeInterface lis : netStatusListener) {
            LogUtil.w("zry", "Wifi2Moblie: " + lis.getClass().getSimpleName());
            lis.onNetWifi2Moblie();
        }
    }

    private void notifyListenerNetChange() {
        for (NetStatusChangeInterface lis : netStatusListener) {
            LogUtil.w("zry", "NetChange: " + lis.getClass().getSimpleName());
            lis.onNetChange();
        }
    }

    /**
     * wifi 改变，网络广播，检测不到
     *
     * @return
     */
    private boolean isWifiChange() {
        String prevSsid = NetUtil.mPrevSsid;
        String currSsid = NetUtil.getConnectingWifiDevice().getSsid();
        if (prevSsid == null || currSsid == null) {
            LogUtil.v("zry", "prevSsid == null || currSsid == null");
            return false;
        }
        if (!currSsid.equals(prevSsid)) {
            LogUtil.v("zry", "mPrevSsid: " + prevSsid + " currSsid: " + currSsid);
            NetUtil.mPrevSsid = currSsid;
            return true;
        }
        LogUtil.w("zry", "isWifiChange no");
        return false;
    }

}

