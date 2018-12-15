package com.minicreate.online_taxi.transmission.protocol.beidou;

import com.minicreate.online_taxi.transmission.protocol.AbstractBaseParam;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

public class BaseParamBeidou extends AbstractBaseParam {
    private static final String TAG = "BaseParamBeidou";
    protected int result = -1;
    protected byte[] value = null;
    private String carId;//终端手机号。传递字符串即可，使用的时候会自动转换成BCD码
    protected byte[] content;
    private int id = -1;//消息ID，最好使用16进制

    public BaseParamBeidou(int id) {
        result = -1;
        this.id = id;
    }

    public void setCarID(String carId) {
        this.carId = carId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getToken() {
        return id;
    }

    /**
     * 将实体类转化为协议数据
     */
    public byte[] parseToProtocol() {
        //首先打包内容
        packContent();
        if (content != null) {
            LogUtil.d(TAG, "content = " + BytesUtil.BytestoHexStringPrintf(content));
        }
        //总长度为：协议头（1）+消息头（15）+消息体（n）+校验码（1）+协议尾（1）
        int contentLen = 0;
        if (content != null) {
            contentLen = content.length;
        }
        //TODO 暂时用来测试，没有消息包封装项。正常情况下，会额外多出3个字节。
        byte[] data = new byte[18 + contentLen - 3];//TODO 消息包封装项暂时没有
        int index = 0;
        //协议头，1位
        data[index] = 0x7e;
        index++;
        //消息ID,2位
        data[index] = (byte) ((id >> 8) & 0xFF);
        index++;
        data[index] = (byte) (id & 0xFF);
        index++;
        //消息体属性，2位。里面包含有内容长度
        //TODO 暂时是用来测试的，所以保留位、分包位、数据加密方式都是0，跟在后面的是内容长度
        //TODO 长度占用10个位，位于高八位的最后两位和低八位
        data[index] = (byte) ((contentLen >> 8) & 0x3);//高八位后面两位的长度
        index++;
        data[index] = (byte) (contentLen & 0xFF);
        index++;
        //终端手机号，6位，采用压缩BCD码。
        byte[] carIdArrays = changeToBCD(carId);
        System.arraycopy(carIdArrays, 0, data, index, carIdArrays.length);
        index += 6;
        //消息流水号，2位
        data[index++] = 0;
        data[index++] = 1;
        //消息体
        if (content != null) {
            System.arraycopy(content, 0, data, index, content.length);
            index += contentLen;
        }
        //异或校验码，不包括第一位
        LogUtil.d(TAG, "index = " + index);
        int jiaoyan = NetUtil.xor_jiaoyan(data, 1, index);
        data[index] = (byte) jiaoyan;
        index++;
        //协议尾
        data[index] = 0x7e;
        LogUtil.d(TAG, "data = " + BytesUtil.BytestoHexStringPrintf(data));
        //TODO 数据转义，待补充
        return data;
    }

    /**
     * 将协议数据解析成实体类
     * <p>
     * 记得先反转义
     */
    public void parseFromProtocol(byte[] src) {

    }

    /**
     * 打包子类的内容，这里使用模板方法模式
     */
    protected void packContent() {

    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public int getValueLen() {
        if (value == null) {
            return 0;
        } else {
            return value.length;
        }
    }

    /**
     * 将src转换为BCD码，返回的byte数组有6位
     *
     * @param src
     * @return
     */
    private static byte[] changeToBCD(String src) {
        src = src.trim();
        byte[] data = new byte[6];
        // 首先判断字符是否是12位，不到的在前面补0
        int len = src.length();
        if (src.length() < 12) {
            for (int i = 0; i < (12 - len); i++) {
                src = "0" + src;
            }
        }
        byte num1, num2;
        for (int i = 0; i < data.length; i++) {
            char c1 = src.charAt(i * 2);
            char c2 = src.charAt(i * 2 + 1);
            num1 = (byte) (Byte.parseByte("" + c1) << 4);// 高位
            num2 = (byte) (Byte.parseByte("" + c2) & 0xF);// 低位
            data[i] = (byte) (num1 + num2);
        }
        return data;
    }
}
