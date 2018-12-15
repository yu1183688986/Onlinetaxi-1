package com.minicreate.online_taxi.transmission.strategy;

import android.content.Context;

import com.minicreate.online_taxi.config.EndpointConfig;
import com.minicreate.online_taxi.transmission.BusinessFlowManager;
import com.minicreate.online_taxi.transmission.Endpoint;
import com.minicreate.online_taxi.transmission.EndpointManager;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.UnvarnishedTransmissionHi3520DV300;
import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.protocol.dianchuang.HeartbeatDianchuang;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

import java.util.List;

/**
 * 0x24开头的协议策略类
 */
public class ProtocolDCStrategy implements Strategy {
    private static final String TAG = "ProtocolDCStrategy";

    @Override
    public int getToken(Param param) {
        return param.getToken();
    }

    /**
     * 一定记得先要反转义
     *
     * @param src
     * @return
     */
    @Override
    public int getToken(byte[] src) {
        //第七位是命令字
        return src[7];
    }

    @Override
    public byte[] escape(byte[] unescapedBuffer) {
        return new byte[0];
    }

    @Override
    public byte[] unescape(byte[] escapedBuffer) {
        //TODO 反转义代码要记得补上
        return escapedBuffer;
    }

    @Override
    public byte[] parseToProtocol(Param param) {
        return param.parseToProtocol();
    }

    @Override
    public Param parseFromProtocol(byte[] src, Param baseParam) {
        baseParam.parseFromProtocol(src);
        return baseParam;
    }

    /**
     * 测试数据完整性，测试校验码是否正确
     * <p>
     * 这个方法可以统一使用
     *
     * @param src
     * @return
     */
    @Override
    public boolean checkData(byte[] src) {
        return true;
    }

    /**
     * 协议头是0x24，协议尾是0x0A
     *
     * @param src
     * @return
     */
    @Override
    public List<byte[]> findPackage(byte[] src) {
        return NetUtil.findPackageByDifferentHeadAndEnd(src, 0x24, 0x0A);
    }

    @Override
    public Param getHeartbeat(Context context) {
        HeartbeatDianchuang heartbeatDianchuang = new HeartbeatDianchuang(0x5B);
        heartbeatDianchuang.setCarID("123456");
        return heartbeatDianchuang;
    }

    @Override
    public long getHeartbeatPeriod() {
        return 0;
    }

    @Override
    public boolean handleMessage(byte[] src, Context context) {
        LogUtil.d(TAG, "handleMessage src = " + BytesUtil.BytestoHexStringPrintf(src));
        //转发给Hi3520DV300
        //首先拆包
        //获取内容长度
        UnvarnishedTransmissionHi3520DV300 param = new UnvarnishedTransmissionHi3520DV300(0x50, context);//填什么一样，其实不需要参数
        param.setContent(src);
        LogUtil.d(TAG, "dianchuang zhuanfaBuf = " + BytesUtil.BytestoHexStringPrintf(param.parseToProtocol()));
        LogUtil.d("hjw_test", "dianchuang zhuanfaBuf = " + BytesUtil.BytestoHexStringPrintf(param.parseToProtocol()));
        //首先需要判断服务器是否连接上了
        if (EndpointManager.get().getEndpointById(EndpointConfig.USB.getName()) != null) {
            BusinessFlowManager.get(EndpointConfig.USB.getName(), context).sendSetupCommand(new OnResponseListener() {
                @Override
                public void onResponse(Param result) {
                    LogUtil.d(TAG, "onResponse");
                }
            }, param, param.parseToProtocol(), param.getToken());
        } else {
            //USB设备端没连接上
            LogUtil.e(TAG, "USB设备端没连接上");
        }
        return true;
    }
}
