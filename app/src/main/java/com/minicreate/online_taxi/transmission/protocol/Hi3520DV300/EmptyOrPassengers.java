package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * SC20发送给3520D空车或载客的信息
 */
public class EmptyOrPassengers extends BaseParamHi3520DV300 {
    private int number;

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public EmptyOrPassengers(Context context) {
        super(0x08, context);
        setCommand(0x45);
    }
}
