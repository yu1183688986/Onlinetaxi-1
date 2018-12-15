package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.io.UnsupportedEncodingException;

public class Upgrade_03 extends BaseParamHi3520DV300 {
    private static final String TAG = "Upgrade_03";
    private int fileNameLength;//文件名长度
    private String fileName = "";//文件名
    private int currentPackage;//当前包号
    private int state;//状态

    public Upgrade_03(Context context) {
        super(0x03, context);
        setCommand(0x48);
    }

    public void setFileNameLength(int fileNameLength) {
        this.fileNameLength = fileNameLength;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileNameLength() {
        return fileNameLength;
    }

    public String getFileName() {
        return fileName;
    }

    public int getCurrentPackage() {
        return currentPackage;
    }

    public void setCurrentPackage(int currentPackage) {
        this.currentPackage = currentPackage;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        LogUtil.d(TAG, "src = " + BytesUtil.BytestoHexStringPrintf(src));
        //子命令内容从数组下标19开始
        int index = 19;
        //升级文件名长度
        int height = (src[index++] & 0xff) << 8;
        int low = src[index++] & 0xff;
        fileNameLength = height + low;
        //文件名
        byte[] tmp = new byte[fileNameLength];
        for (int i = 0; i < fileNameLength; i++) {
            tmp[i] = src[index + i];
        }
        index += fileNameLength;
        try {
            fileName = new String(tmp, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //当前包号
        currentPackage += (src[index++] & 0xff) << 8;
        currentPackage += src[index++] & 0xff;
        //状态
        state = src[index++];
    }

    @Override
    public String toString() {
        return "fileNameLength = " + fileNameLength + " ,fileName = " + fileName + " ,currentPackage = " + currentPackage + " ,state = " + state;
    }
}
