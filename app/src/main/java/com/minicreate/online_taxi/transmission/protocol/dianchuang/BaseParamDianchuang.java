package com.minicreate.online_taxi.transmission.protocol.dianchuang;

import com.minicreate.online_taxi.common.Constants;
import com.minicreate.online_taxi.transmission.protocol.AbstractBaseParam;
import com.minicreate.online_taxi.utils.NetUtil;

import java.util.Arrays;

public class BaseParamDianchuang extends AbstractBaseParam {
    private static final String TAG = "BaseParam";
    protected int result = -1;
    protected byte[] value = null;
    private byte[] carId;//车辆编号
    private int command = -1;//命令字，默认是-1，用来判断到底赋值了没有

    public BaseParamDianchuang(int command) {
        result = -1;
        this.command = command;
    }

    public void setCarID(String carId) {
        this.carId = carId.getBytes();
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    /**
     * 将实体类转化为协议数据
     */
    public byte[] parseToProtocol() {
        if (carId == null) {
            //车辆编号不能为空
            throw new RuntimeException("CarId is null!");
        }
        if (command == -1) {
            throw new RuntimeException("Command is null!");
        }
        int contentLen = 0;//内容的长度
        if (value != null) {
            contentLen = value.length;
        }

        /**
         * 首先计算总长度
         * 总长度=协议头（1位）+车辆编号（6位）+命令字（1位）+预留字节（1位）+长度（2位）+内容（0-n位）+校验和（1位）+结束符（1位）
         * 除了内容外，其他地方有13位
         * 所以整段协议帧的总长度=13+内容长度
         */
        byte[] data = new byte[13 + contentLen];
        int destPos = 0;//偏移位置
        //协议头
        data[destPos] = 0x24;
        destPos++;
        //车辆编号，一般都是数字
        System.arraycopy(carId, 0, data, destPos, carId.length);
        destPos += carId.length;
        //命令字
        data[destPos] = (byte) command;
        destPos++;
        //预留字节
        data[destPos] = 0;
        destPos++;
        //内容长度
        data[destPos] = (byte) ((contentLen >> 8) & 0xff);//高位
        destPos++;
        data[destPos] = (byte) (contentLen & 0xff);
        destPos++;
        //内容
        if (value != null) {
            System.arraycopy(value, 0, data, destPos, value.length);
            destPos += value.length;
        }
        //校验和，不包括第一位
        int jiaoyan = NetUtil.xor_jiaoyan(data, 1, destPos);
        data[destPos] = (byte) jiaoyan;
        destPos++;
        //协议尾
        data[destPos] = 0x0A;
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
     * 获取协议标识符
     *
     * @return
     */
    public int getToken() {
        return command;
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

    public int parsePushParam(byte[] resp, int index, int len) {
        if (resp == null) {
            return Constants.RESP_DATA_NULL;
        }
        if (len == 0) {
            return Constants.RESP_LEN_0;
        }

        return 0;
    }

    /**
     * 将接收到的数据按照0x7e协议解析
     *
     * @param resp
     * @param index
     * @param len
     * @return
     */
    public int parseSetingParam_0x7e(byte[] resp, int index, int len) {
        if (resp == null) {
            return Constants.RESP_DATA_NULL;
        }
        if (len == 0) {
            return Constants.RESP_LEN_0;
        }
        return 0;
    }

    /**
     * 将接收到的数据按照0x24协议解析
     *
     * @param resp
     * @param index
     * @param len
     * @return
     */
    public int parseSetingParam_0x24(byte[] resp, int index, int len) {
        if (resp == null) {
            return Constants.RESP_DATA_NULL;
        }
        if (len == 0) {
            return Constants.RESP_LEN_0;
        }
        return 0;
    }

    public int parseQueryParam(byte[] resp, int index, int len) {
        if (resp == null) {
            return Constants.RESP_DATA_NULL;
        }
        if (len == 0) {
            return Constants.RESP_LEN_0;
        }
        return 0;
    }

    public int parseUpgradeParam(byte[] resp, int index, int len) { // 目前没有这个回应
        if (resp == null) {
            return Constants.RESP_DATA_NULL;
        }
        if (len == 0) {
            return Constants.RESP_LEN_0;
        }
        return 0;
    }

    public int parseAskforReportParam(byte[] resp, int index, int len) {
        if (resp == null) {
            return Constants.RESP_DATA_NULL;
        }
        if (len == 0) {
            return Constants.RESP_LEN_0;
        }
        return 0;
    }

    /**
     * 如果发送的数据携带参数，必须实现这个方法，把数据打包
     */
    public boolean packDataForSend() {
        return false;
    }

    @Override
    public String toString() {
        return "ParamBase [token=" + getToken() + ", result=" + result + ", value="
                + Arrays.toString(value) + "]";
    }
}
