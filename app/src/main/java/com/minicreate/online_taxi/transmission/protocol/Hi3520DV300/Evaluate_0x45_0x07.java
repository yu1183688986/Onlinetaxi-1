package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 客户对司机的评价，由SC20发送给3520D
 */
public class Evaluate_0x45_0x07 extends BaseParamHi3520DV300 {
    private int evaluate;//评价

    public void setEvaluate(int evaluate) {
        this.evaluate = evaluate;
    }

    public int getEvaluate() {
        return evaluate;
    }

    public Evaluate_0x45_0x07(Context context) {
        super(0x07, context);
        setCommand(0x45);
    }

    @Override
    protected void packContent() {
        content = new byte[1];
        content[0] = (byte) evaluate;
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        evaluate = src[19] & 0xff;
    }
}
