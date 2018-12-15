package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;


import android.content.Context;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

public class Net1Param_22Hi3520DV300 extends BaseParamHi3520DV300 {
    private static final String TAG = "Net1Param_22Hi3520DV300";
    private String domain1;//域名1
    private String domain2;//域名2
    private String domain3;//域名3
    private String domain4;//域名4
    private String apn1;
    private String ip1;
    private String port1;
    private String ip2;
    private String port2;
    private String ip3;
    private String port3;
    private String ip4;
    private String port4;

    public Net1Param_22Hi3520DV300(int id, Context context) {
        super(id,context);
        setCommand(0x46);
    }

    public String getApn1() {
        return apn1;
    }

    public void setApn1(String apn1) {
        this.apn1 = apn1;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getPort1() {
        return port1;
    }

    public void setPort(String port) {
        this.port1 = port;
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        LogUtil.d(TAG, "parseFromProtocol src = " + BytesUtil.BytestoHexStringPrintf(src));
        Entry entry = new Entry();
        int index = 19;//第一个长度从19开始
        //APN长度
        entry = getValue(index, src, entry);
        apn1 = entry.value;
        //域名1
        entry = getValue(entry.index, src, entry);
        domain1 = entry.value;
        //IP1
        entry = getValue(entry.index, src, entry);
        ip1 = entry.value;
        //端口1
        entry = getValue(entry.index, src, entry);
        port1 = entry.value;

        //域名2
        entry = getValue(entry.index, src, entry);
        domain2 = entry.value;
        //IP2
        entry = getValue(entry.index, src, entry);
        ip2 = entry.value;
        //端口2
        entry = getValue(entry.index, src, entry);
        port2 = entry.value;

        //域名3
        entry = getValue(entry.index, src, entry);
        domain3 = entry.value;
        //IP3
        entry = getValue(entry.index, src, entry);
        ip3 = entry.value;
        //端口3
        entry = getValue(entry.index, src, entry);
        port3 = entry.value;

        //域名4
        entry = getValue(entry.index, src, entry);
        domain4 = entry.value;
        //IP4
        entry = getValue(entry.index, src, entry);
        ip4 = entry.value;
        //端口4
        entry = getValue(entry.index, src, entry);
        port4 = entry.value;

        LogUtil.d(TAG, "domain1 = " + domain1 + " ,ip1 = " + ip1 + " ,port1 = " + port1);
        LogUtil.d(TAG, "domain2 = " + domain2 + " ,ip2 = " + ip2 + " ,port2 = " + port2);
        LogUtil.d(TAG, "domain3 = " + domain3 + " ,ip3 = " + ip3 + " ,port3 = " + port3);
        LogUtil.d(TAG, "domain4 = " + domain4 + " ,ip4 = " + ip4 + " ,port4 = " + port4);
    }

    private Entry getValue(int index, byte[] src, Entry e) {
        String s = "";
        int len = ((src[index] << 8) & 0xff) + (src[index + 1] & 0xff);
        for (int i = 0; i < len; i++) {
            s += "" + (char) src[index + 2 + i];
        }
        LogUtil.d(TAG, "index = " + index + " ,len = " + len + " ,s = " + s);
        e.index = index + 2 + len;
        e.value = s;
        return e;
    }

    private class Entry {
        public int index;
        public String value = "";
    }
}
