package com.minicreate.online_taxi.transmission;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.strategy.Strategy;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.ThreadPoolUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BusinessFlow {

    private final String TAG = BusinessFlow.class.getSimpleName();
    private static final long TIMEOUT = 5000;
    private static final int MSG_TIMEOUT = 1;
    private static final int MSG_COMMAND = 2;
    private static final int MSG_HEARTBEAT = 3;
    private Context context;

    public static final int TIMEOUT_ERROR_CODE = -1000;

    private Endpoint endpoint; // 业务绑定的端点
    private Strategy strategy;//协议策略接口
    private AtomicInteger sequence = new AtomicInteger();
    //private Beatheart_F0 beatheart = new Beatheart_F0();
    private Param beatHeart;
    private byte[] beatHeartData;
    private long beatHeartPeriod;//心跳间隔

    //应答实体，里面包含OnResponseListener和BaseParam
    public static class ResponseEntry {
        public OnResponseListener onResponseListener;
        public Param param;

        public ResponseEntry(OnResponseListener onResponseListener, Param param) {
            this.onResponseListener = onResponseListener;
            this.param = param;
        }
    }

    //TODO 新增实体类：Value<OnResponseListener,BaseParam>，OnResponseListener得修改，多添加一个BaseParam参数
    private Map<Integer, ResponseEntry> businessCacheWaitResp =
            new ConcurrentHashMap<Integer, ResponseEntry>();

    public BusinessFlow() {
    }

    public void init(String name, Context context) {
        this.context = context;
        endpoint = EndpointManager.get().getEndpointById(name);
        strategy = endpoint.getProtocolStrategy();
        //初始化心跳包
        beatHeart = strategy.getHeartbeat(context);
        beatHeartPeriod = strategy.getHeartbeatPeriod();
        if (beatHeartPeriod == 0) {
            //默认为5s
            beatHeartPeriod = 5000;
        }
        beatHeartData = beatHeart.parseToProtocol();
    }

    private void sendMessageDelayedVh(int token) {
        Message message = handler.obtainMessage(MSG_TIMEOUT, token, 0, null);
        handler.sendMessageDelayed(message, TIMEOUT);
    }

    public void sendMessage(int token, String src) {
        Message message = handler.obtainMessage(MSG_COMMAND, token, 0, src);
        handler.sendMessage(message);
    }

    public void sendMessage(int token, byte[] src) {
        Message message = handler.obtainMessage(MSG_COMMAND, token, 0, src);
        handler.sendMessage(message);
    }

    public void responseBusiness(int token, String src) {
        sendMessage(token, src);
    }

    /**
     * 反转义并验证数据有效性
     *
     * @param src
     */
    public void responseBusiness(byte[] src) {
        // 反转义
        src = strategy.unescape(src);

        if (src == null) {
            LogUtil.e(TAG, "src == null");
            return;
        }
        // 判断数据是否完整，校验码是否正确
        boolean isDataCorrect = strategy.checkData(src);
        if (!isDataCorrect) {
            //数据不正确
            LogUtil.e(TAG, "数据不正确");
            return;
        }

        boolean continueHandle = strategy.handleMessage(src, context);
        //这是要处理转发的
        if (continueHandle) {
            return;
        }
        // 获取协议的认证码，认证码一般是命令字
        int token = strategy.getToken(src);
        sendMessage(token, src);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            final int token = msg.arg1;
            LogUtil.d(TAG, "token = " + token + " ,what = " + msg.what);
            final byte[] message = (byte[]) msg.obj;

            switch (msg.what) {
                case MSG_COMMAND:
                    ResponseEntry responseEntry = businessCacheWaitResp.get(token);
                    if (responseEntry != null) {
                        OnResponseListener response = responseEntry.onResponseListener;
                        removeWaitRespCache(token);
                        removeMessages(token); // 删除超时队列
                        //TODO 检查协议数据是否是WANControl发出的，只需要判断标识符是否一致即可。使用接收到的byte数组中获取到的认证码，与保存在ResponseEntry中的BaseParam的认证码进行比较
                        if (token != strategy.getToken(responseEntry.param)) {
                            //数据不对，不是我们请求的
                            LogUtil.e(TAG, "token incorrupt receive token = " + token + " ,send token = " + strategy.getToken(responseEntry.param));
                            break;
                        }
                        //TODO 将协议解析为实体类
                        Param param = strategy.parseFromProtocol(message, responseEntry.param);
                        //TODO 将协议实体类传递给回调接口
                        response.onResponse(param);
                    } else {
                        LogUtil.e(TAG, "!!!!!!!!!!!! get(token) null !!!!!!!!!!!! token = " + token);
                    }
                    break;

                case MSG_TIMEOUT:
                    ResponseEntry responseTimeoutEntry = businessCacheWaitResp.get(token);
                    if (responseTimeoutEntry != null) {
                        final OnResponseListener responseTimeout = responseTimeoutEntry.onResponseListener;
                        removeWaitRespCache(token);
                        LogUtil.w(TAG, "timeout, remove(token): " + token);
                        // 超时之后，就发送回原来的BaseParam，BaseParam的超时标记要置为true
                        responseTimeoutEntry.param.setTimeOut(true);
                        responseTimeout.onResponse(responseTimeoutEntry.param);
                    }
                    break;
                case MSG_HEARTBEAT: // 5 s后发心跳
                    sendSetupCommand(HeartbeatResp, beatHeart, message, strategy.getToken(beatHeart));
                    break;
            }
        }
    };

    private void sendBytesDate(final byte[] data) {
        LogUtil.d(TAG, "sendBytesDate");
        ThreadPoolUtil.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                endpoint.addReqData(data);
            }
        });
    }

    private void addToWaitRespCache(int token, OnResponseListener l, Param param) {
        ResponseEntry entry = new ResponseEntry(l, param);
        businessCacheWaitResp.put(token, entry);
    }

    public void removeWaitRespCache(int token) {
        businessCacheWaitResp.remove(token);
    }

    private void setReqTimeoutVh(int token) {
        sendMessageDelayedVh(token);
    }

    private OnResponseListener HeartbeatResp =
            new OnResponseListener() {
                @Override
                public void onResponse(Param result) {
                    if (result.isTimeOut()) {
                        LogUtil.d(TAG, "心跳超时 token = " + result.getToken() + " ,endpoint.getName = " + endpoint.getName() + " ,endpoint = " + endpoint);
                        //心跳超时了就直接重连
                        endpoint.restartEndpoint();
                    } else {
                        //收到了心跳包，继续发送
                        LogUtil.d(TAG, "Heartbeat RET_OK " + result.getToken());
                        sendHeartbeat();
                    }
                }
            };

    public void sendHeartbeat() {
        //TODO 这儿要获取车辆ID
        LogUtil.d(TAG, "sendHeartbeat token = " + String.format("%02x", beatHeart.getToken()));
        Message message = handler.obtainMessage(MSG_HEARTBEAT, strategy.getToken(beatHeart), 0, beatHeartData);
        handler.sendMessageDelayed(message, beatHeartPeriod);
    }

    public void cancelHeartbeat() {
        handler.removeCallbacksAndMessages(null);
    }

    public void sendSetupCommand(OnResponseListener l, Param param) {
        sendSetupCommand(l, param, param.parseToProtocol(), param.getToken());
    }

    public void sendSetupCommand(OnResponseListener l, Param param, byte[] values, int cmdId) {
        sendDateLogicVh(l, param, values, cmdId);
    }

    /**
     * 发送数据，没有超时检测，并且也不做回调方法
     *
     * @param values
     */
    public void sendAskForReprotCommand(byte[] values) {
        sendBytesDate(values);              // 发送数据
    }

    private void sendDateLogicVh(OnResponseListener l, Param param, byte[] values, int cmdId) {
        addToWaitRespCache(cmdId, l, param); // 添加响应回调到缓存，等待回调
        sendBytesDate(values);              // 发送数据
        setReqTimeoutVh(cmdId);          // 超时处理
    }
}
