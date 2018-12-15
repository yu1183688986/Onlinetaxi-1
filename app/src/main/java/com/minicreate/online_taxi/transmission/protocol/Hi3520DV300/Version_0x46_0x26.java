package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.LogUtil;

import java.io.UnsupportedEncodingException;

/**
 * Hi3520D软件版本号查询
 */
public class Version_0x46_0x26 extends BaseParamHi3520DV300 {
    private static final String TAG = "Version_0x46_0x26";
    private String version;//版本号

    public Version_0x46_0x26(Context context) {
        super(0x26, context);
        setCommand(0x46);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        int subLen = getSubCommandLen(src);
        byte[] tmp = new byte[subLen];
        for (int i = 0; i < subLen; i++) {
            tmp[i] += src[19 + i];
        }
        try {
            version = new String(tmp, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "version = " + version);
    }
}
