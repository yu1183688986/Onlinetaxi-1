package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.LogUtil;

public class VehicleNo_20 extends BaseParamHi3520DV300 {
    private static final String TAG = "VehicleNo_20";

    private String vehicleNo = "";

    public VehicleNo_20(Context context) {
        super(0x20, context);
        setCommand(0x46);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        //子命令内容长度
        int subLen = getSubCommandLen(src);
        LogUtil.d(TAG, "subLen = " + subLen);
        //子命令内容，从数组19开始
        for (int i = 0; i < subLen; i++) {
            vehicleNo += "" + (char) src[19 + i];
        }
        LogUtil.d(TAG, "vehicleNo = " + vehicleNo);
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    @Override
    public String toString() {
        return "VehicleNo_20 [vehicleNo=" + vehicleNo + "]";
    }
}
