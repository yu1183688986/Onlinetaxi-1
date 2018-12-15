package com.minicreate.online_taxi.transmission.protocol.beidou;

/**
 * 终端注销
 */
public class Logout extends BaseParamBeidou {
    /**
     * 终端注销的消息ID是0x0003
     */
    public Logout() {
        super(0x0003);
    }
}
