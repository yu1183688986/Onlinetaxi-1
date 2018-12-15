package com.minicreate.online_taxi.transmission;

import android.util.Log;

import com.minicreate.online_taxi.transmission.listener.TcpRecvListener;
import com.minicreate.online_taxi.transmission.strategy.ProtocalHi3520DV300Strategy;
import com.minicreate.online_taxi.transmission.strategy.Strategy;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Clock
 * @version V1.0
 * @ClassName: TcpReceiver.java
 * @Date 2014-9-19 上午11:54:33
 * @Description: TCP端点 接收线程
 */
public class TcpReceiver extends Thread {
    private InputStream in;
    private Boolean isForceStop = false;
    private final String TAG = "TcpReceiver";
    private TcpRecvListener listeners;
    private Strategy strategy;

    public TcpReceiver(InputStream in, TcpRecvListener tdl, Strategy strategy) {
        this.in = in;
        listeners = tdl;
        this.strategy = strategy;
    }

    public TcpReceiver(Socket socket, TcpRecvListener tdl, Strategy strategy) {
        try {
            this.strategy = strategy;
            socket.setSoTimeout(0);
            in = socket.getInputStream();
            listeners = tdl;
            isForceStop = false;
            d("TcpReceiver 构造函数。。。。。。。。。。。。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void notifyDataReceive(byte[] buf, int len) {
        listeners.onReceive(buf, len);
    }

    @Override
    public void run() {
        d("TcpReceiver run...");
        int errorCount = 0;
        byte[] recvArray;
        if (strategy instanceof ProtocalHi3520DV300Strategy) {
            //3520D的缓冲区需要设置大一些
            recvArray = new byte[16 * 1024];
        } else {
            recvArray = new byte[1024];
        }
        List<byte[]> packageList;

        while (!isInterrupted()) {
            try {
                int length = 0;
                length = in.read(recvArray);//流的末尾会返回-1, 像你这种情况就是当对方将socket的输出流关闭后， 你将对方的输出都读完后，再读下一个字节就会返回-1.
                LogUtil.v(TAG, "本次包，接收到的长度：" + length);
                if (length > 0) {
                    byte[] src = new byte[length];
                    System.arraycopy(recvArray, 0, src, 0, length);
                    List<byte[]> resultList = strategy.findPackage(src);
                    if (resultList != null) {
                        LogUtil.d(TAG, "resultList.size = " + resultList.size());
                        for (byte[] tmp : resultList) {
                            LogUtil.d(TAG, "receive token = " + String.format("%02x", strategy.getToken(strategy.unescape(src))) + " ,data = " + BytesUtil.BytestoHexStringPrintf(tmp));
                            notifyDataReceive(tmp, tmp.length);
                        }
                    } else {
                        LogUtil.d(TAG, "resultList is null");
                    }
                } else if (length < 0) {
                    errorCount++;
                    d("接收长度为负，接收到10次负数会关闭接收线程，当前第" + errorCount + "次");
                    if (errorCount > 10) {
                        break;
                    }
                } else {
                    //接收到的长度是0
                }
            } catch (SocketException e) {
                break;
            } catch (IOException e) {
                break;
            }
        }
        stopException();
    }

    public void stopException() {
        if (!isForceStop) {
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    listeners.onRecvNetException();
                }
            }, 1000);
        }
    }

    public void stopForce() {
        isForceStop = true;
        stopRun();
    }

    public void stopRun() {
        interrupt();
        close();
    }

    private void close() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void d(String msg) {
        Log.w(TAG, msg);
    }
}
