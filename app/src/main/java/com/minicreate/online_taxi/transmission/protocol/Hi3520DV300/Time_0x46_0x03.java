package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 时间、日期、星期几显示
 * TODO 暂时不用处理
 */
public class Time_0x46_0x03 extends BaseParamHi3520DV300 {
    public Time_0x46_0x03(Context context) {
        super(0x03, context);
        setCommand(0x46);
    }
}
