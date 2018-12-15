package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 关机模式
 */
public class OffMode_0x46_0x21 extends BaseParamHi3520DV300 {
    private int offMode;//关机模式
    private int hour;//关机时间，小时
    private int minute;//关机时间，分钟

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public OffMode_0x46_0x21(Context context) {
        super(0x21, context);
        setCommand(0x46);
    }

    @Override
    protected void packContent() {
        content = new byte[3];
        content[0] = (byte) offMode;
        content[1] = (byte) hour;
        content[2] = (byte) minute;
    }
}
