package com.minicreate.online_taxi.transmission.protocol;

import com.minicreate.online_taxi.transmission.ProtocolVh;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

public class EncodeVh {

    private final String TAG = EncodeVh.class.getSimpleName();

    private static EncodeVh instance = new EncodeVh();

    private EncodeVh() {
    }

    ;

    public static EncodeVh get() {
        return instance;
    }

    /**
     * 从厂商编号到用户数据依次累加的累加和，然后取累加的低8位作为校验码
     *
     * @param data
     * @return
     */
    private byte calcCheckCode(byte[] data) {
        int len = data.length - 1;
        int result = 0;
        for (int index = 4; index < len; index++) {
            result += data[index];
        }
//		LogUtil.d(TAG, "CheckCode: " + result);
        byte temp = (byte) (result & 0xFF);
//		LogUtil.d(TAG, "CheckCode byte: " + temp);
        return temp;
    }

    /**
     * 若校验码、消息头以及消息体中出现0x7e，则要进行转义处理，转义规则定义如下：
     * 0x7e   -------------------   0x7d + 0x02
     * 0x7d   -------------------   0x7d + 0x01
     *
     * @param src 发送消息时：消息封装  ---- 计算并填充校验码 ------ 转义；
     *            接收消息时：转义还原 ---  验证校验码              ------ 解析消息；
     */
    private byte[] escapeCharacter7e(byte[] src) {
        int escapeCnt = 0;
        int srcLen = src.length - 1; // 末尾的7e

//		LogUtil.i(TAG, "escapeCharacter7e src: " + BytesUtil.BytestoHexStringPrintf(src));
        for (int index = 1; index < srcLen; index++) {  // 头部的7e
            if (src[index] == 0x7e || src[index] == 0x7d) {
                escapeCnt++;
            }
        }
        if (escapeCnt == 0) {
//			LogUtil.d(TAG, "没有转义符号 走");
            return src;
        }
        LogUtil.d(TAG, "转义字母数escapeCnt: " + escapeCnt);
        byte[] des = new byte[src.length + escapeCnt];
        des[0] = src[0]; // 头部的7e
        int cnt = 1;
        for (int index = 1; index < srcLen; index++, cnt++) {  // 头部的7e
            if (src[index] == 0x7e) {
                des[cnt] = 0x7d;
                cnt++;
                des[cnt] = 0x02;
            } else if (src[index] == 0x7d) {
                des[cnt] = 0x7d;
                cnt++;
                des[cnt] = 0x01;
            } else {
                des[cnt] = src[index];
            }
        }

        des[cnt] = src[srcLen]; // 末尾的7e

        LogUtil.d(TAG, "转义后 des: " + BytesUtil.BytestoHexStringPrintf(des));
        return des;
    }

    /**
     * 打包协议数据包
     */
    public byte[] makeSetupFrame(Param... cmds) {
        return new byte[0];
    }

    private int makeFrameHead(byte data[]) {
        int index = 0;
        data[index++] = (byte) ProtocolVh.SYNC_FLAG_HEADER;
        index++; //校验码
        data[index++] = (byte) ProtocolVh.VERSION_0;
        data[index++] = (byte) ProtocolVh.VERSION_1;

        data[index++] = (byte) ProtocolVh.CORP_NO_0;
        data[index++] = (byte) ProtocolVh.CORP_NO_1;

        data[index++] = (byte) ProtocolVh.PERIP_TYPE_NO;
        return index;
    }

    private int makeFrameCmd(byte data[], int cmdType, int index) {
        data[index++] = (byte) cmdType;// 命令类型
        return index;
    }

    private int makeFrameUserDataHead(byte data[], int userDataLen, int index) {
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x00;
        data[index++] = (byte) 0x00; // 6位车辆编号
        // 用户数据的总长度
        data[index++] = (byte) (userDataLen >> 8 & 0xFF); // 数据的高字节保存在内存的低地址，大端
        data[index++] = (byte) (userDataLen & 0xFF);
        return index;
    }

    private int makeFrameTail(byte data[], int index) {
        data[index++] = (byte) ProtocolVh.SYNC_FLAG_END;
        return index;
    }

    private void makeFrameCheckCode(byte data[]) {
        byte temp = calcCheckCode(data);
        data[ProtocolVh.INDEX_CHECK_CODE] = temp;
    }

//	static byte[] temp = {
//	(byte) 0xa5
//	,0x00 ,0x28 
//	,0x00 ,0x02 
//	,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 
//	,(byte) 0xc0 ,(byte) 0xa8 ,0x04 ,0x01 
//	,(byte) 0xc0 ,(byte) 0xa8 ,0x04 ,0x02 
//	,0x00 
//	,0x30 ,0x30 ,0x30 ,0x31 
//	,0x01 
//	,0x14 ,0x01 ,0x00 
//	,0x15 ,0x04 ,(byte) 0xff ,(byte) 0xff ,(byte) 0xff ,(byte) 0xff 
//	,0x16 ,0x06 ,0x18 ,(byte) 0xfe ,0x34 ,(byte) 0xf1 ,0x65 ,(byte) 0x8a 
//	,0x04 ,0x0a ,0x54 ,0x47 ,0x2d ,0x4b ,0x43 ,0x47 ,0x47 ,0x47 ,0x33 ,0x00 
//	,(byte) 0xbd ,0x5a };

}
