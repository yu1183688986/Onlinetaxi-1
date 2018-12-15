/**
 * Copyright © 2014 Aristone Tech Co.,Ltd. All rights reserved.
 *
 * @FileName: TcpEndpoint.java
 * @author Clock
 * email: zhongruiyun@aristonetech.com
 * @date 2014-9-19
 * @version V1.0
 * @Description:
 * @Modification History:
 * @Date Author        Version        Discription
 * -------------------------------------------------------------
 * 2014-9-19       Clock          1.0
 */

package com.minicreate.online_taxi.transmission;

import android.content.Context;
import android.util.Log;

import com.minicreate.online_taxi.transmission.listener.NetStatusChangeInterface;
import com.minicreate.online_taxi.transmission.listener.TcpConnectListener;
import com.minicreate.online_taxi.transmission.listener.TcpRecvListener;
import com.minicreate.online_taxi.transmission.strategy.Strategy;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Clock
 * @version V1.0
 * @ClassName: TcpEndpoint.java
 * @Date 2014-9-19 上午11:55:57
 * @Description: TCP端点，监听TCP，连接、接收、发送
 */

public class TcpEndpoint extends Endpoint implements TcpConnectListener,
        TcpRecvListener, NetStatusChangeInterface {
    protected String ip;
    protected int port;
    private final String TAG = "TcpEndpoint";
    private Socket TcpSocket;

    private TcpConnector connector;
    private TcpReceiver receiver;
    private TcpSender sender;
    private TCPDispatcher Dispatcher;
    private Timer reConnTimer/* = new Timer()*/;
    private TcpConnectListener tcpConnectListener;

    /**
     * 连接服务器的次数，
     * 连接服务器失败：1、网络问题（路由连不到外网等），2、服务器问题
     */
    private int connServerCnt = 0;
    /**
     * 检查到网络无效后，再重连的次数
     * 1、避免检测故障
     * 2、切换网络时，系统通知延迟，导致检测不到网络变化
     */
    private int reConnCnt = 0;

    public Socket getTcpSocket() {
        return TcpSocket;
    }

    /**
     * 使用ip来充当端点名字
     *
     * @param ip
     * @param port
     * @param strategy
     */
    public TcpEndpoint(String ip, int port, Strategy strategy, Context context) {
        super(ip, strategy, context);
        this.ip = ip;
        this.port = port;
        EndpointManager.get().addEndpoint(this);
        NetStatusMonitor.get().addNetStatusListener(this);
    }

    @Override
    public synchronized void startEndpoint(TcpConnectListener... tcpConnectListener) {
        this.tcpConnectListener = tcpConnectListener[0];
        endpointConnToNet();
    }

    /**
     * 软件启动，无网络，不连接服务器
     * 有网络，无法连接服务器，重复连
     */
    @Override
    protected void endpointConnToNet() {
        d("connToNetwork");
        LogUtil.d(TAG, "curr thread: " + Thread.currentThread().getId());
        connectStatus = CONNECTING;
        TcpSocket = new Socket();
        connector = new TcpConnector(TcpSocket, ip, port, this);
        connector.start();
    }

    @Override
    public void onConnectStart(String server, int port) {
        d("onConnectStart");
    }

    @Override
    public synchronized void onConnectOk(Socket socket, String server, int port) {
        d("onConnectOk server = " + server + " ,port = " + port);
        connectStatus = CONNECT_SUCCESS;
        if (Dispatcher == null) {
            Dispatcher = new TCPDispatcher(getName(), context);
            Dispatcher.start();
        }
        if (sender == null) {
            sender = new TcpSender(TcpSocket);
        }
        if (receiver == null) {
            receiver = new TcpReceiver(TcpSocket, this, getProtocolStrategy());
            receiver.start();
        }
        isEndpointValid = true;
        notifyConnSuccess();
        connServerCnt = 0;
        reConnCnt = 0;

        //责任链模式，传递回调接口方法
        this.tcpConnectListener.onConnectOk(socket, server, port);
    }

    @Override
    public void onConnectOk() {

    }

    private void delayReConnect() {
        d("re conn 5s later");
        connectStatus = CONNECT_WAIT;
        isEndpointValid = false;
        if (reConnTimer == null) {
            reConnTimer = new Timer();
        }
        // 多线程 businessflow restart endpoint 置reConnTimer null
        // 导致下面的reConnTimer空指针异常
        reConnTimer.schedule(new ReConnTask(), 5000);

        connServerCnt++;
        if (connServerCnt > 2) {
            connServerCnt = 0;
            notifyConnServerFailed();
        }
    }

    private class ReConnTask extends TimerTask {

        @Override
        public void run() {
            d("re conn run");
            endpointConnToNet();
        }
    }

    @Override
    public synchronized void onRecvNetException() {
        d("onRecvError");
        if (connectStatus == CONNECT_WAIT || connectStatus == CONNECTING) {
            LogUtil.i(TAG, "onRecvError" + "别的线程在连接: " + connectStatus);
            return;
        }
        connectStatus = CONNECT_EXCEPTION;
        notifyNetBroken();

        receiver.stopRun(); // 出现过空指针异常，反反复复的执行onRecvNetException，没加锁
        receiver = null;

        sender.stopRun();
        sender = null;

        try {
            d("TcpSocket.close");
            TcpSocket.close();
            TcpSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        delayReConnect();
    }

    @Override
    public String getTcpRecvListenerName() {
        return "TcpEndpointListener";
    }

    @Override
    public synchronized void onConnectError(int code, String server, int port) {
        d("onConnectError");
        try {
            d("onConnectError TcpSocket.close");
            if (TcpSocket == null) {
                d("onConnectError TcpSocket == null");
                return;
            }
            TcpSocket.close();
            TcpSocket = null;
        } catch (IOException e) {
            TcpSocket = null;
            d("TcpSocket.close exception: " + e);
        }

        delayReConnect();
    }

    /**
     * 多线程并发调用的方法 心跳
     */
    @Override
    public synchronized void stopEndpoint() {

        notifyEndpointStop();

        if (receiver != null) {
            receiver.stopForce();
            receiver = null;
            d("receiver.stopRun");
        }

        if (sender != null) {
            sender.close();
            sender = null;
        }

        if (reConnTimer != null) {  // businessflow 心跳 没回应 restartEndpoint()，
            // 走到这个方法，然后设置reConnTimer = null，
//			timerIsValid = false;
            reConnTimer.cancel();
            reConnTimer = null;
            d("reConnTimer.cancel");
        }
        if (connector != null) {
            connector.stopRun();
            connector = null;
            d("connector.stopRun");
        }

        if (Dispatcher != null) {
            Dispatcher.stopRun();
            Dispatcher = null;
            d("Dispatcher.stopRun");
        }

//		TcpResender.get().stopRun();

        try {
            d("TcpSocket.close");
            if (TcpSocket != null) {
                TcpSocket.close();
                TcpSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectStatus = CONNECT_INIT;
//		EndpointManager.get().removeEndpoint(this);
    }

    @Override
    public void restartEndpoint() {
        stopEndpoint();
        startEndpoint(tcpConnectListener);
    }

    @Override
    public void closeEndpoint() {
        stopEndpoint();
        EndpointManager.get().removeEndpoint(this);
        removeAllEndpointListener();
//		WrapperManager.get().removeAllWrapper();
//		ParserLib.get().removeAllParser();
        NetStatusMonitor.get().removeNetStatusListener(this);
    }

    @Override
    public synchronized int send(byte[] buf, int length) {
        if (sender != null) {
            d("...send...:" + BytesUtil.BytestoHexStringPrintf(buf, length)/**/);
            sender.send(buf, length);
        } else {
            d("===== sender =====null ");
        }
        return 0;
    }

    @Override
    public void onReceive(byte[] buf, int len) {
        if (Dispatcher != null) {
            Dispatcher.addRespData(buf);
        } else {
            // 连接状态 突然断网  连接成功 Dispatcher还没创建完，收到数据，导致异常
            d("===== Dispatcher =====null ");
        }
    }

    @Override
    public void onReceiveBegin() {

    }

    @Override
    public void onReceiveEnd() {

    }

    void d(String msg) {
        Log.w(TAG, msg);
    }

    @Override
    public void onNetOn2Off() {

    }

    /**
     * 等待网络恢复，重连服务器
     * 多线程并发调用的方法，网络接上
     */
    @Override
    public synchronized void onNetOff2On() {
//		synchronized (mConnLock) {
//			// 等待正在连接的操作，避免冲突
//		}
        if (connectStatus == CONNECT_WAIT || connectStatus == CONNECTING) {
            LogUtil.i(TAG, "onNetOff2On" + "别的线程在连接" + connectStatus);
            return;
        }
        if (reConnTimer != null) {
            reConnTimer.cancel();
            reConnTimer = null;
            d("onNetOff2On reConnTimer.cancel");
        }
        if (TcpSocket == null) {    //不在连接状态
            endpointConnToNet();
        } else {
            // 1、别的操作正在连TcpSocket ing
            // 2、前一瞬间，别的操作已经连TcpSocket成功
            d("onNetOff2On 但是前面已经有在连了 TcpSocket != null");
        }
    }

    @Override
    public void onNetWifi2Moblie() {

    }

    @Override
    public void onNetMoblie2Wifi() {

    }

    @Override
    public void onNetChange() {

    }

    private boolean isNetAvailable() {
        return NetUtil.isAvailable(null);
    }
}
