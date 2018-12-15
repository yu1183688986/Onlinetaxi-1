package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.LogUtil;

/**
 * GPS数据发送周期查询
 */
public class GPSPeriod_0x46_0x25 extends BaseParamHi3520DV300 {
    private static final String TAG = "GPSPeriod_0x46_0x25";
    private int gpsPeriod;//GPS数据发送周期
    private int gpsPeriod_bubiao1;//部标1 GPS数据发送周期
    private int gpsPeriod_bubiao2;//部标2 GPS数据发送周期
    private int gpsPeriod_905;//905 GPS数据发送周期

    public GPSPeriod_0x46_0x25(Context context) {
        super(0x25, context);
        setCommand(0x46);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        int subLen = getSubCommandLen(src);
        if (subLen != 16) {
            LogUtil.e(TAG, "subLen is not 16 :" + subLen);
            return;
        }
        //19位开始
        int index = 19;
        gpsPeriod += (src[index++] & 0xff) << 24;
        gpsPeriod += (src[index++] & 0xff) << 16;
        gpsPeriod += (src[index++] & 0xff) << 8;
        gpsPeriod += src[index++] & 0xff;

        gpsPeriod_bubiao1 += (src[index++] & 0xff) << 24;
        gpsPeriod_bubiao1 += (src[index++] & 0xff) << 16;
        gpsPeriod_bubiao1 += (src[index++] & 0xff) << 8;
        gpsPeriod_bubiao1 += src[index++] & 0xff;

        gpsPeriod_bubiao2 += (src[index++] & 0xff) << 24;
        gpsPeriod_bubiao2 += (src[index++] & 0xff) << 16;
        gpsPeriod_bubiao2 += (src[index++] & 0xff) << 8;
        gpsPeriod_bubiao2 += src[index++] & 0xff;

        gpsPeriod_905 += (src[index++] & 0xff) << 24;
        gpsPeriod_905 += (src[index++] & 0xff) << 16;
        gpsPeriod_905 += (src[index++] & 0xff) << 8;
        gpsPeriod_905 += src[index++] & 0xff;
    }
}
