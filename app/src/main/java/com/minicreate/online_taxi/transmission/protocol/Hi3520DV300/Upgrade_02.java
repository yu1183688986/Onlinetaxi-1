package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.utils.LogUtil;

import java.io.UnsupportedEncodingException;

/**
 * 发送升级数据包
 */
public class Upgrade_02 extends BaseParamHi3520DV300 {
    public static int BYTE_LEN = 15 * 1024;
    private static final String TAG = "Upgrade_02";
    private int direction;//升级方向
    private int fileType;//文件类型
    private String fileName;//文件名
    private int fileNameLength;//文件名长度
    private long fileSize;//文件总大小
    private int totalPackage;//总包数
    private int currentPackage;//当前包序号
    private int currentPackageSize;//当前包数据大小，指的是文件内容的大小，不是这段协议的大小
    private volatile byte[] fileData;//文件数据

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getTotalPackage() {
        return totalPackage;
    }

    public void setTotalPackage(int totalPackage) {
        this.totalPackage = totalPackage;
    }

    public int getCurrentPackage() {
        return currentPackage;
    }

    public void setCurrentPackage(int currentPackage) {
        this.currentPackage = currentPackage;
    }

    public int getCurrentPackageSize() {
        return currentPackageSize;
    }

    public void setCurrentPackageSize(int currentPackageSize) {
        this.currentPackageSize = currentPackageSize;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public Upgrade_02(Context context) {
        super(0x02, context);
        setCommand(0x48);
    }

    @Override
    protected void packContent() {
        //子命令内容总长度，固定16+文件名长度+数据长度
        content = new byte[14 + fileName.length() + fileData.length];
        LogUtil.d(TAG, "content.length = " + content.length + " ,fileName = " + fileName + " ,fileData.length = " + fileData.length);
        int index = 0;
        content[index++] = (byte) direction;
        content[index++] = (byte) fileType;
        //文件名长度
        content[index++] = (byte) (fileName.length() << 8);
        content[index++] = (byte) (fileName.length() & 0xff);
        //文件名
        byte[] tmp = fileName.getBytes();
        for (int i = 0; i < tmp.length; i++) {
            content[index + i] = tmp[i];
        }
        index += tmp.length;
        //文件总大小
        content[index++] = (byte) (fileSize >> 24);
        content[index++] = (byte) (fileSize >> 16);
        content[index++] = (byte) (fileSize >> 8);
        content[index++] = (byte) (fileSize & 0xff);
        //总包数
        content[index++] = (byte) (totalPackage >> 8);
        content[index++] = (byte) (totalPackage & 0xff);
        //当前包序号
        content[index++] = (byte) (currentPackage >> 8);
        content[index++] = (byte) (currentPackage & 0xff);
        //当前包数据大小，2位
        content[index++] = (byte) (currentPackageSize >> 8);
        content[index++] = (byte) (currentPackageSize & 0xff);

        //数据
        for (int i = 0; i < fileData.length; i++) {
            content[index++] = fileData[i];
        }
    }

    @Override
    public void parseFromProtocol(byte[] src) {
        //数据从数组第19位开始
        int subLen = getSubCommandLen(src);
        //文件类型
        int index = 19;
        index++;
        fileType = src[index++] & 0xff;
        //文件名长度
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
            LogUtil.e(TAG, "文件名出错");
        }
        LogUtil.d(TAG, "fileName = " + fileName);
        //文件总大小
        fileSize += (src[index++] & 0xff) << 24;
        fileSize += (src[index++] & 0xff) << 16;
        fileSize += (src[index++] & 0xff) << 8;
        fileSize += src[index++] & 0xff;
        LogUtil.d(TAG, "fileSize = " + fileSize);

        //总包数
        totalPackage += (src[index++] & 0xff) << 8;
        totalPackage += src[index++] & 0xff;
        LogUtil.d(TAG, "totalPackage = " + totalPackage);

        //当前包序号
        currentPackage += (src[index++] & 0xff) << 8;
        currentPackage += src[index++] & 0xff;
        LogUtil.d(TAG, "currentPackage = " + currentPackage);

        //当前包数据大小
        currentPackageSize += (src[index++] & 0xff) << 8;
        currentPackageSize += src[index++] & 0xff;
        LogUtil.d(TAG, "currentPackageSize = " + currentPackageSize);

        //数据
        fileData = new byte[currentPackageSize];
        for (int i = 0; i < currentPackageSize; i++) {
            fileData[i++] = src[index + i];
        }
    }

    @Override
    public String toString() {
        return "direction = " + direction + " ,fileType = " + fileType + " ,fileName = " + fileName +
                " ,fileNameLength = " + fileNameLength + " ,fileSize = " + fileSize +
                " ,totalPackage = " + totalPackage + " ,currentPackage = " + currentPackage +
                " ,currentPackageSize = " + currentPackageSize;
    }
}
