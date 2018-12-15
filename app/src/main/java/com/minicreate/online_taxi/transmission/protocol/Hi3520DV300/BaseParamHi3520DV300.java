package com.minicreate.online_taxi.transmission.protocol.Hi3520DV300;

import android.content.Context;

import com.minicreate.online_taxi.transmission.protocol.AbstractBaseParam;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;
import com.minicreate.online_taxi.utils.PreferencesUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * TODO 所有的协议问题，包括协议过长、数据不对等，都可以以抛异常的方式提示
 */
public class BaseParamHi3520DV300 extends AbstractBaseParam {
    private Context context;
    private static final String TAG = "BaseParamHi3520DV300";
    protected int id = -1;//子命令类型，最好使用16进制
    private int command = -1;//命令类型
    protected volatile byte[] content;//子命令内容
    private String vehicleNo;//车辆编号

    public void setCommand(int command) {
        this.command = command;
    }

    public BaseParamHi3520DV300(int id, Context context) {
        this.context = context;
        this.id = id;
        vehicleNo = PreferencesUtil.get(context, PreferencesUtil.NAME).get(PreferencesUtil.VehicleNo, "123456");
        //TODO 车辆编号默认为123456
        vehicleNo = "123456";
    }

    @Override
    public synchronized byte[] parseToProtocol() {
        if (command == -1) {
            LogUtil.e(TAG, "命令类型有问题");
        }
        //首先打包内容
        packContent();
        //总长度为：协议头（1）+校验码（1）+版本号（2）+厂商编号（2）+外设类型编号（1）+命令类型（1）+用户数据（n）+协议尾（1）
        int contentLen = 0;
        if (content != null) {
            contentLen = content.length;
        }

        byte[] data = new byte[20 + contentLen];
        int destPos = 0;//偏移位置
        //协议头
        data[destPos] = 0x7e;
        destPos++;
        //越过校验码，后面再补充
        destPos++;
        //版本号
        data[destPos++] = 0;
        data[destPos++] = 0;
        //厂商编号
        data[destPos++] = 0;
        data[destPos++] = 1;
        //外设类型编号
        data[destPos++] = 0x03;
        //命令类型，command
        data[destPos++] = (byte) command;

        //下面是用户数据，首先是车辆编号，车辆编号是以gbk方式编码的
        int i = destPos;
        byte[] tmp;
        //如果车辆编号不足6位，那么就提示个错误
        if (vehicleNo.length() < 6) {
            LogUtil.e(TAG, "车辆编号小于6位");
        }
        try {
            tmp = vehicleNo.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //编码出错，那么车辆编号就默认为123456
            tmp = "123456".getBytes();
        }
        //这里只接收6位，如果超出了就不管
        if (tmp.length > 6) {
            LogUtil.e(TAG, "车辆编号超过了6位 length = " + tmp.length);
        }
        for (; i < destPos + 6; i++) {
            data[i] = tmp[i - destPos];
        }
        destPos += 6;
        //子命令总长度为子命令内容长度+3
        if (content != null) {
            data[destPos++] = (byte) (((content.length + 3) >> 8) & 0xff);
            data[destPos++] = (byte) ((content.length + 3) & 0xff);

            //子命令
            data[destPos++] = (byte) id;
            //子命令内容长度
            data[destPos++] = (byte) ((content.length >> 8) & 0xff);
            data[destPos++] = (byte) (content.length & 0xff);
            LogUtil.d(TAG, "content.lenght = " + content.length);
        } else {
            data[destPos++] = (byte) (((0 + 3) >> 8) & 0xff);
            data[destPos++] = (byte) ((0 + 3) & 0xff);

            //子命令
            data[destPos++] = (byte) id;
            //子命令内容长度
            data[destPos++] = (byte) ((0 >> 8) & 0xff);
            data[destPos++] = (byte) (0 & 0xff);
            LogUtil.d(TAG, "content.lenght = 0");
        }

        if (content != null) {
            System.arraycopy(content, 0, data, destPos, content.length);
            destPos += content.length;
        }
        //协议尾
        data[destPos] = 0x7e;
        //计算校验码，从厂商编号到用户数据依次累加的累加和，然后取累加的低8位作为校验码
        int jiaoyan = NetUtil.jiaoyan(data, 4, destPos);
        data[1] = (byte) jiaoyan;
        //发之前要转义
        //LogUtil.d(TAG, "data.size = " + data.length + " ,destPos = " + destPos + " ,data[dest] = " + data[destPos] + " data = " + Arrays.toString(data));
//        LogUtil.d(TAG, "data = " + BytesUtil.BytestoHexStringPrintf(data));
        byte[] result = escape(data);
//        LogUtil.d(TAG, "result = " + BytesUtil.BytestoHexStringPrintf(result));
        return result;
    }

    @Override
    public void parseFromProtocol(byte[] src) {

    }

    /**
     * 转义
     *
     * @param unescapedBuffer
     * @return
     */
    private static byte[] escape(byte[] unescapedBuffer) {
        int num = 0;
        // 找出除了头尾之外所有的7e和7d
        for (int i = 1; i < unescapedBuffer.length - 1; i++) {
            if (((unescapedBuffer[i] & 0xff) == 0x7e) || ((unescapedBuffer[i] & 0xff) == 0x7d)) {
                num++;
            }
        }
        byte[] data = new byte[unescapedBuffer.length + num];
        int index = 1;
        for (int i = 1; i < unescapedBuffer.length - 1; i++) {
            if ((unescapedBuffer[i] & 0xff) == 0x7e) {
                data[index++] = 0x7d;
                data[index++] = 0x02;
            } else if ((unescapedBuffer[i] & 0xff) == 0x7d) {
                data[index++] = 0x7d;
                data[index++] = 0x01;
            } else {
                data[index++] = unescapedBuffer[i];
            }
        }
        // 前后补上7e
        data[0] = 0x7e;
        data[data.length - 1] = 0x7e;
        return data;
    }

    /**
     * 该协议的命令字就是id
     *
     * @return
     */
    @Override
    public int getToken() {
        return id;
    }

    /**
     * 打包子类的内容，这里使用模板方法模式
     */
    protected void packContent() {

    }

    /**
     * 获取数组中的子命令内容长度
     */
    protected int getSubCommandLen(byte[] src) {
        if (src != null) {
            //第17、18位
            int height = (src[17] >> 8) & 0xff;
            int low = src[18] & 0xff;
            int subLen = height + low;
            return subLen;
        } else {
            return 0;
        }
    }
}
