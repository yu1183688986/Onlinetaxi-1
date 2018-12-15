package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

/**
 * 硬盘状态查询
 */
public class HardDisk_0x46_0x31 extends BaseParamHi3520DV300 {
    private int hardDiskState;//硬盘状态
    private int recordState;//录像状态
    private int motherboardChip;//底板芯片状态
    private int camera;//摄像头状态

    public HardDisk_0x46_0x31(Context context) {
        super(0x31, context);
        setCommand(0x46);
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        int index = 19;
        hardDiskState = src[index++] & 0xff;
        recordState = src[index++] & 0xff;
        motherboardChip = src[index++] & 0xff;
        //摄像头状态，4位
        //TODO 暂时不做，有点奇怪，一个状态需要4位？
    }
}
