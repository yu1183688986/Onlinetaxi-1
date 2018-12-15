package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 停止实时数据上报
 */
public class ShutOffCurrentTime_0x47_0xFF extends BaseParamHi3520DV300 {
    public ShutOffCurrentTime_0x47_0xFF(Context context) {
        super(0xFF, context);
        setCommand(0x47);
    }
}
