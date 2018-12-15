package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 短消息列表
 * TODO 待补充
 */
public class SMSList_0x46_0x14 extends BaseParamHi3520DV300 {

    public SMSList_0x46_0x14(Context context) {
        super(0x14, context);
        setCommand(0x46);
    }
}
