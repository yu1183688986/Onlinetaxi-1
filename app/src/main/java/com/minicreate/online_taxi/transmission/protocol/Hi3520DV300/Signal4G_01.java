package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 网络信号强度、Socket连接状态（0x01）等信息
 */
public class Signal4G_01 extends BaseParamHi3520DV300 {
    int signal;//网络信号值
    int socket1;
    int socket2;
    int socket3;
    int socket4;
    int acc;
    int sd2State;

    public void setSd2State(int sd2State) {
        this.sd2State = sd2State;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public void setSocket1(int socket1) {
        this.socket1 = socket1;
    }

    public void setSocket2(int socket2) {
        this.socket2 = socket2;
    }

    public void setSocket3(int socket3) {
        this.socket3 = socket3;
    }

    public void setSocket4(int socket4) {
        this.socket4 = socket4;
    }

    public Signal4G_01(int id, Context context) {
        super(id, context);
        setCommand(0x45);
    }

    @Override
    protected void packContent() {
        content = new byte[24];
        int index = 0;
        content[index++] = (byte) signal;
        content[index++] = (byte) socket1;
        content[index++] = (byte) socket2;
        content[index++] = (byte) socket3;
        content[index++] = (byte) socket4;
        content[index++] = (byte) acc;
        content[index++] = (byte) sd2State;
        for (; index < content.length; index++) {
            content[index] = 0;
        }
    }
}
