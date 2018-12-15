package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 短消息上报
 */
public class SMSReporting_0x45_0x03 extends BaseParamHi3520DV300 {
    private int isReport;//是否播报
    private String data;//内容

    public SMSReporting_0x45_0x03(Context context) {
        super(0x03, context);
        setCommand(0x45);
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setIsReport(int isReport) {
        this.isReport = isReport;
    }

    public int getIsReport() {
        return isReport;
    }

}
