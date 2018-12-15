package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 拨打电话0x04
 */
public class PhoneCall_0x45_0x04 extends BaseParamHi3520DV300 {
    private int state;//状态
    private int phoneCallState;//通话状态

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setPhoneCallState(int phoneCallState) {
        this.phoneCallState = phoneCallState;
    }

    public int getPhoneCallState() {
        return phoneCallState;
    }

    public PhoneCall_0x45_0x04(Context context) {
        super(0x04, context);
        setCommand(0x45);
    }
}
