package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

public class HeartbeatHi3520DV300 extends BaseParamHi3520DV300 {
    public HeartbeatHi3520DV300(Context context) {
        super(0x49, context);
        setCommand(0x49);
    }
}