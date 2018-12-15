package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

public class Upgrade_01 extends BaseParamHi3520DV300 {
    private int number;

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public Upgrade_01(Context context) {
        super(0x01, context);
        setCommand(0x48);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        //回复码固定在数组第19位
        number = src[19];
    }

    @Override
    protected void packContent() {
        content = new byte[1];
        content[0] = (byte) number;
    }
}
