package com.minicreate.online_taxi.transmission.strategy;

import android.content.Context;

import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.protocol.beidou.HeartbeatBeidou;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

import java.util.List;

/**
 * 北斗的协议
 */
public class ProtocolBeiDouStrategy implements Strategy {
    private static final String TAG = "ProtocolBeiDouStrategy";

    @Override
    public int getToken(Param param) {
        return param.getToken();
    }

    /**
     * 一定记得先要反转义
     * <p>
     * 北斗的平台通用应答的消息ID是请求应答的消息ID
     * 但是注册应答是一个特例，和注册请求的消息ID不同
     *
     * @param src
     * @return 返回标识码，如果标识码未找到，那么返回-1
     */
    @Override
    public int getToken(byte[] src) {
        //首先拿到消息ID，第1、2位是消息ID，需要合并
        //高位
        int height = (src[1] & 0xff) << 8;
        //低位
        int low = src[2] & 0xff;
        int messageId = height + low;
        int token = -1;
        if (messageId == 0x8001) {
            //平台通用应答
            //
        } else if (messageId == 0x8100) {
            //终端注册应答
            token = 0x0100;
        } else {
            //消息ID错误，token会返回-1
            LogUtil.d(TAG, "未找到标识码");
            //获取出终端的请求消息ID，第5、6就是终端请求ID
            //高位
            height = (src[5] & 0xff) << 8;
            //低位
            low = src[6] & 0xff;
            token = height + low;
        }
        return token;
    }

    @Override
    public byte[] escape(byte[] unescapedBuffer) {
        return new byte[0];
    }

    /**
     * 反转义
     *
     * @param escapedBuffer
     * @return
     */
    @Override
    public byte[] unescape(byte[] escapedBuffer) {
        return NetUtil.unescape_0x7e_0x7d(escapedBuffer);
    }

    @Override
    public byte[] parseToProtocol(Param param) {
        return new byte[0];
    }

    @Override
    public Param parseFromProtocol(byte[] src, Param baseParam) {
        baseParam.parseFromProtocol(src);
        return baseParam;
    }

    //TODO 检查数据是否正确，代码偷懒没写，默认是true
    @Override
    public boolean checkData(byte[] src) {
        return true;
    }

    /**
     * 协议头是0x7e，协议尾是0x7e
     *
     * @param src
     * @return
     */
    @Override
    public List<byte[]> findPackage(byte[] src) {
        return NetUtil.findPackageBySameHeadAndEnd(src, 0x7e);
    }

    @Override
    public Param getHeartbeat(Context context) {
        HeartbeatBeidou heartbeatBeidou = new HeartbeatBeidou(0x2);
        heartbeatBeidou.setCarID("12345678901");
        return heartbeatBeidou;
    }

    @Override
    public long getHeartbeatPeriod() {
        return 0;
    }

    @Override
    public boolean handleMessage(byte[] src, Context context) {
        return false;
    }
}
