package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

public class GPSHi3520DV300 extends BaseParamHi3520DV300 {
    private static final String TAG = "GPSHi3520DV300";
    private String gps = "";

    public String getGps() {
        return gps;
    }

    public GPSHi3520DV300(Context context) {
        super(0x02, context);
        setCommand(0x45);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        //子命令内容长度
        int subLen = getSubCommandLen(src);
        LogUtil.d(TAG, "subLen = " + subLen);
        //子命令内容，从数组19开始
        for (int i = 0; i < subLen; i++) {
            gps += "" + (char) src[19 + i];
        }
        LogUtil.d(TAG, "gps = " + gps + " ,src = " + BytesUtil.BytestoHexStringPrintf(src));
    }
}
