package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 报警
 */
public class Alarm_0x45_0x09 extends BaseParamHi3520DV300 {
    private int number;

    public Alarm_0x45_0x09(int id, Context context) {
        super(id, context);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    protected void packContent() {
        content = new byte[1];
        content[0] = (byte) number;
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        number = src[19] & 0xff;
    }
}
