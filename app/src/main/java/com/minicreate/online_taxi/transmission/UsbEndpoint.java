package com.minicreate.online_taxi.transmission;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;

import com.minicreate.online_taxi.transmission.listener.TcpConnectListener;
import com.minicreate.online_taxi.transmission.listener.TcpRecvListener;
import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.strategy.Strategy;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * USB连接端点
 */
public class UsbEndpoint {
    private static final String TAG = "UsbEndpoint";
    private UsbManager usbManager;
    private Context context;
    private TcpReceiver tcpReceiver;
    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inStream;
    private FileOutputStream outStream;
    private TcpConnectListener tcpConnectListener;
    private String name;
    private Strategy strategy;
    private Timer reConnTimer;
    private Map<Integer, BusinessFlow.ResponseEntry> businessCacheWaitResp =
            new ConcurrentHashMap<Integer, BusinessFlow.ResponseEntry>();
    private Param beatHeart;
    private byte[] beatHeartData;

    private int beatHeartPeriod = 5000;//心跳延时，多久发一次心跳

    private static final long TIMEOUT = 5000;//超时检测，5秒一次
    private static final int MSG_TIMEOUT = 1;
    private static final int MSG_COMMAND = 2;
    private static final int MSG_HEARTBEAT = 3;

    public UsbEndpoint(Context context, String name, Strategy strategy) {
        this.name = name;
        this.context = context;
        this.strategy = strategy;
        beatHeart = strategy.getHeartbeat(context);
        beatHeartData = beatHeart.parseToProtocol();
    }

    public void startEndpoint(TcpConnectListener... tcpConnectListener) {
        LogUtil.d(TAG, "startEndpoint");
        this.tcpConnectListener = tcpConnectListener[0];
        usbManager = (UsbManager) this.context.getSystemService(Context.USB_SERVICE);

        final UsbAccessory[] accessoryList = usbManager.getAccessoryList();
        d("accessoryList = " + accessoryList);
        if (accessoryList == null || accessoryList.length == 0) {
            e("no accessory found");
            //TODO 这儿没找到设备，需要重连
            this.tcpConnectListener.onConnectError(0, null, 0);
        } else {
            LogUtil.d(TAG, "accessoryList.length = " + accessoryList.length);
            openAccessory(accessoryList[0]);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            final int token = msg.arg1;
            LogUtil.d(TAG, "token = " + String.format("%02x", token) + " ,what = " + msg.what);
            final byte[] message = (byte[]) msg.obj;

            switch (msg.what) {
                case MSG_COMMAND:
                    BusinessFlow.ResponseEntry responseEntry = businessCacheWaitResp.get(token);
                    if (responseEntry != null) {
                        OnResponseListener response = responseEntry.onResponseListener;
                        businessCacheWaitResp.remove(token);//删除超时队列
                        if (token != strategy.getToken(responseEntry.param)) {
                            //数据不对，不是我们请求的
                            LogUtil.e(TAG, "token incorrupt receive token = " + token + " ,send token = " + strategy.getToken(responseEntry.param));
                            break;
                        }
                        Param param = strategy.parseFromProtocol(message, responseEntry.param);
                        LogUtil.d(TAG, "onResponse token = " + String.format("%02x", token));
                        response.onResponse(param);
                    } else {
                        LogUtil.e(TAG, "MSG_COMMAND !!!!!!!!!!!! get(token) null !!!!!!!!!!!! token = " + String.format("%02x", token));
                    }
                    break;

                case MSG_TIMEOUT:
                    BusinessFlow.ResponseEntry responseTimeoutEntry = businessCacheWaitResp.get(token);
                    if (responseTimeoutEntry != null) {
                        final OnResponseListener responseTimeout = responseTimeoutEntry.onResponseListener;
                        businessCacheWaitResp.remove(token);
                        // 超时之后，就发送回原来的BaseParam，BaseParam的超时标记要置为true
                        responseTimeoutEntry.param.setTimeOut(true);
                        responseTimeout.onResponse(responseTimeoutEntry.param);
                        LogUtil.d(TAG, "timeout, remove(token): " + String.format("%02x", token));
                    } else {
                        //没超时
                    }
                    break;
                case MSG_HEARTBEAT: // 5 s后发心跳
                    send(heartbeatResp, beatHeart);
                    LogUtil.d(TAG, "MSG_HEARTBEAT sendBeatHeart");
                    break;
            }
        }
    };

    private OnResponseListener heartbeatResp =
            new OnResponseListener() {
                @Override
                public void onResponse(Param result) {
                    if (result.isTimeOut()) {
                        LogUtil.d(TAG, "心跳超时 token = " + String.format("%02x", result.getToken()));
                        //心跳没了，要重连
                        tcpConnectListener.onConnectError(0, null, 0);
                    } else {
                        //收到了心跳包，继续发送
                        LogUtil.d(TAG, "Heartbeat RET_OK " + String.format("%02x", result.getToken()));
                        sendHeartbeat();
                    }
                }
            };

    public void stopEndpoint() {
        LogUtil.d(TAG, "stopEndpoint");
        if (inStream != null) {
            try {
                inStream.close();
                inStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            try {
                outStream.close();
                outStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (tcpReceiver != null) {
            tcpReceiver.stopForce();
            tcpReceiver = null;
            d("receiver.stopRun");
        }

        try {
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            fileDescriptor = null;
        }
    }

    public void restartEndpoint() {
        LogUtil.d(TAG, "restartEndpoint currentThread = " + Thread.currentThread().toString());
        stopEndpoint();
        startEndpoint(tcpConnectListener);
    }

    public void closeEndpoint() {
        LogUtil.d(TAG, "closeEndpoint");
        stopEndpoint();
    }

    /**
     * 发送数据
     *
     * @param l
     * @param param
     * @return
     */
    public synchronized int send(OnResponseListener l, Param param) {
        byte[] buf = param.parseToProtocol();
        d("...send...:" + BytesUtil.BytestoHexStringPrintf(buf));
        //发送数据
        if (outStream != null) {
            try {
                outStream.write(buf);
            } catch (IOException e) {
                LogUtil.e(TAG, "e = " + e.toString());
                e.printStackTrace();
                // 发送端断开了，需要重连
                tcpConnectListener.onConnectError(0, null, 0);
                return 0;
            }
            //添加超时队列
            int token = strategy.getToken(buf);
            BusinessFlow.ResponseEntry entry = new BusinessFlow.ResponseEntry(l, param);
            businessCacheWaitResp.put(token, entry);
            //发送超时检测
            Message message = handler.obtainMessage(MSG_TIMEOUT, token, 0, null);
            handler.sendMessageDelayed(message, TIMEOUT);
        }
        return 0;
    }

    public void sendHeartbeat() {
        LogUtil.d(TAG, "sendHeartbeat token = " + String.format("%02x", beatHeart.getToken()));
        Message message = handler.obtainMessage(MSG_HEARTBEAT, strategy.getToken(beatHeart), 0, beatHeartData);
        handler.sendMessageDelayed(message, beatHeartPeriod);
    }

    private void openAccessory(UsbAccessory accessory) {
        e("openAccessory");
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {
            //添加端点
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inStream = new FileInputStream(fd);
            outStream = new FileOutputStream(fd);
            //打开Receiver线程
            tcpReceiver = new TcpReceiver(inStream, new TcpRecvListener() {
                @Override
                public void onReceive(byte[] src, int len) {
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

                    //有些数据是从下位机传上来的，就不需要处理超时了，直接返回
                    boolean continueHandle = strategy.handleMessage(src, context);
                    if (continueHandle) {
                        return;
                    }
                    // 获取协议的认证码，认证码一般是命令字
                    int token = strategy.getToken(src);
                    Message message = handler.obtainMessage(MSG_COMMAND, token, 0, src);
                    handler.sendMessage(message);
                }

                @Override
                public void onReceiveBegin() {

                }

                @Override
                public void onReceiveEnd() {

                }

                @Override
                public void onRecvNetException() {
                    LogUtil.e(TAG, "onRecvNetException");
                    //这儿是USB断开后，输入流报的错，在这儿也能进行重连，但是，明明没连USB，usbManager.getAccessoryList()却显示大于0。
                    //因此将重连操作放在输出流，我们保持着输出流不关闭，不断使用输出流发送心跳，如果有异常，那么就重连，这样就不用担心usbManager.getAccessoryList()出错，因为输出流每次发送数据都会检测对方。
                    //TODO 对方断开了连接，需要重连
                    if (tcpReceiver != null) {
                        tcpReceiver.stopRun();
                    }
                    if (outStream != null) {
                        try {
                            outStream.close();
                            outStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    tcpReceiver = null;
                    tcpConnectListener.onConnectError(0, null, 0);
                }

                @Override
                public String getTcpRecvListenerName() {
                    return "USBEndpointListener";
                }
            }, strategy);
            tcpReceiver.start();
            d("onConnectOk");
            tcpConnectListener.onConnectOk();
        } else {
            e("could not connect");
            //TODO 找到了设备但是没连接上，要重连
            tcpConnectListener.onConnectError(0, null, 0);
        }
        d("openAccessory finished");
    }

    private void d(String msg) {
        LogUtil.d(TAG, msg);
    }

    private void e(String msg) {
        LogUtil.e(TAG, msg);
    }
}
