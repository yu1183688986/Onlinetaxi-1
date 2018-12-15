package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 倒车影像
 */
public class BackUpImage extends BaseParamHi3520DV300 {
    private int number;

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public BackUpImage(Context context) {
        super(0x76, context);
        setCommand(0x45);
    }
}
