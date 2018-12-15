package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.io.UnsupportedEncodingException;

public class LicensePlateNumber_27 extends BaseParamHi3520DV300 {
    private static final String TAG = "LicensePlateNumber_27";

    private String licensePlateNumber = "";

    public LicensePlateNumber_27(Context context) {
        super(0x27, context);
        setCommand(0x46);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        //子命令内容长度
        int subLen = getSubCommandLen(src);
        LogUtil.d(TAG, "subLen = " + subLen + " ,src = " + BytesUtil.BytestoHexStringPrintf(src));
        //子命令内容，从数组19开始
        byte[] tmp = new byte[subLen];
        for (int i = 0; i < subLen; i++) {
            tmp[i] = src[19 + i];
        }
        try {
            LogUtil.d(TAG, "tmp = " + BytesUtil.BytestoHexStringPrintf(tmp));
            licensePlateNumber = new String(tmp, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "carNo = " + licensePlateNumber);
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }
}
