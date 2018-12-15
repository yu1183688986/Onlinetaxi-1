package com.minicreate.online_taxi.transmission;

import android.util.Log;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TcpSender extends Thread {
    private OutputStream out;

    public TcpSender(OutputStream out) {
        this.out = out;
    }

    public TcpSender(Socket socket) {
        try {
            if (socket.isConnected() && !socket.isClosed()) {
                this.out = socket.getOutputStream();
                d("TcpSender 构造函数。。。。。。。。。。。。");
            } else {
                d("构造函数 TcpSender socket is closed...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data, int len) {
        if (out != null) {
            try {
                d(BytesUtil.BytestoHexStringPrintf(data));
                LogUtil.d("hjw_test", "send:" + BytesUtil.BytestoHexStringPrintf(data));
                out.write(data, 0, len);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                d("IOException");
            }
        }
    }

    public void close() {
        try {
            d("out.close");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRun() {
        close();
    }

    private void d(String msg) {
        final String TAG = "TcpSender";
        LogUtil.d(TAG, msg);
    }
}
