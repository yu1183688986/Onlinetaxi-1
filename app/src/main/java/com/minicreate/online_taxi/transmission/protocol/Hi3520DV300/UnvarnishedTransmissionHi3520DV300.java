package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

/**
 * Hi3520DV300的透传
 */
public class UnvarnishedTransmissionHi3520DV300 extends BaseParamHi3520DV300 {
    private static final String TAG = "UnvarnishedTransmissionHi3520DV300";
    private byte[] data;

    public UnvarnishedTransmissionHi3520DV300(int id, Context context) {
        super(id, context);
        setCommand(id);
    }

    @Override
    protected void packContent() {
        LogUtil.d(TAG, "packContent data = " + BytesUtil.BytestoHexStringPrintf(data));
        content = data;
    }

    public void setContent(byte[] data) {
        this.data = data;
    }
}
