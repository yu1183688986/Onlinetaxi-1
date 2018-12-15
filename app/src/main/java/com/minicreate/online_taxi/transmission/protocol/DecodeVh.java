package com.minicreate.online_taxi.transmission.protocol;

import com.minicreate.online_taxi.transmission.ProtocolVh;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.ThreadPoolUtil;

public class DecodeVh {

    private final String TAG = DecodeVh.class.getSimpleName();

    private static DecodeVh instance = new DecodeVh();

    private DecodeVh() {
    }

    public static DecodeVh get() {
        return instance;
    }

    public byte[] unescapeCharacter7e(byte[] src) {

//		LogUtil.d(TAG, "src unescapeCharacter7e : " + BytesUtil.BytestoHexStringPrintf(src));
        int srcLen = src.length;
        int unescapeCnt = 0;
        for (int i = 0; i < srcLen; i++) {
            if (src[i] == 0x7d) {
                unescapeCnt++;
            }
        }
        if (unescapeCnt == 0) {
//			LogUtil.d(TAG, "unescapeCnt ==0 无转义字符 走");
            return src;
        }
//		LogUtil.w(TAG, "unescapeCnt: " + unescapeCnt);
        byte[] des = new byte[srcLen - unescapeCnt];

        for (int index = 0, cnt = 0; index < srcLen; index++, cnt++) {  // 头部的7e
            if (src[index] == 0x7d) {
                if (src[index + 1] == 0x02) {
                    des[cnt] = 0x7e;
                    index++;
                    LogUtil.w(TAG, "0x7d: 0x7e");
                } else if (src[index + 1] == 0x01) {
                    des[cnt] = 0x7d;
                    index++;
                    LogUtil.w(TAG, "0x7d: 0x7d");
                } else {
                    LogUtil.e(TAG, "unescapeCharacter7e 失败，字符异常！");
                }
            } else {
                des[cnt] = src[index];
            }
        }
        LogUtil.d(TAG, "des unescapeCharacter7e : " + BytesUtil.BytestoHexStringPrintf(des));
        return des;
    }

    public byte[] parseRecvData(byte[] data) {
        // 1、反转义
        data = unescapeCharacter7e(data);
        // 2、验证数据有效性 暂时忽略
        int cmd = data[ProtocolVh.INDEX_CMD] & 0xFF;
        LogUtil.e(TAG, "cmd: " + String.format("%02x", cmd));

        if (cmd == ProtocolVh.CMD_PUSH || cmd == ProtocolVh.CMD_UPGRADE) {
            final byte[] push = data;
            ThreadPoolUtil.get().getThread().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        parsePushUserData(push);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        } else if (cmd == ProtocolVh.CMD_READ) {

        } else if (cmd == ProtocolVh.CMD_WRITE) {

        } else if (cmd == ProtocolVh.CMD_UPGRADE) {

        }
        return data;
    }

    private void parsePushUserData(byte[] data) throws Exception {

        int index = ProtocolVh.INDEX_USER_DATA;
        int userDataLen = getParamLen(data, index);
        index += ProtocolVh.PARAM_LEN;
        LogUtil.e(TAG, "userDataLen: " + userDataLen);

        int currParam;
        int backup;
        int bound = userDataLen + index;
        for (; index < bound; ) {
            currParam = data[index++] & 0xFF;
            backup = index;
            index = parsePushParam(data, currParam, index);
            if (index == backup) {
                LogUtil.w(TAG, "未知参数 ：" + String.format("%02x", currParam));
                int len = getParamLen(data, index);
                index += ProtocolVh.PARAM_LEN;
                LogUtil.w(TAG, "未知参数 Len: " + len);
                index = index + len;
            }
        }
    }

    private int parsePushParam(byte[] data, int currParam, int index) {
        //TODO 下面的代码不懂是干啥的
//        String className = PushParamLib.get().getClassString(currParam);
//        LogUtil.i(TAG, "className: " + className);
//        if (className == null) {
//            LogUtil.e(TAG, "ParamLib get null ");
//            return index;
//        }
//
//        Param desParam;
//        try {
//            desParam = (Param) Class.forName(PushParamLib.PARAM_PACKAGE + className).newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return index;
//        }
//        int len = getParamLen(data, index);
//        index += ProtocolVh.PARAM_LEN;
//        desParam.parsePushParam(data, index, len);
//        return index + len;
        return 0;
    }

    private int getParamLen(byte[] data, int index) {
        int len = data[index++] & 0xFF;
        len = len << 8;
        len = len | (data[index++] & 0xFF);
        return len;
    }

}
