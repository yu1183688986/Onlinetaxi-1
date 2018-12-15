package com.minicreate.online_taxi.transmission;

import android.util.Log;

import com.minicreate.online_taxi.transmission.listener.TcpConnectListener;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TcpConnector extends Thread {
    private TcpConnectListener listener;
    private String ipAddr;
    private int port;
    private Socket connectedSock;

    public TcpConnector(Socket socket, String serverIP, int port, TcpConnectListener tcl) {
        this.connectedSock = socket;
        this.ipAddr = serverIP;
        this.port = port;
        setConnectionListener(tcl);
    }

    private void setConnectionListener(TcpConnectListener cl) {
        this.listener = cl;
    }

    @Override
    public void run() {
        d(Thread.currentThread().getId() + " run... connect to (" + ipAddr + ":" + port + ")");

        boolean error = false;
        try {
            SocketAddress socketAddress;
            socketAddress = new InetSocketAddress(ipAddr, port);
            notifyOnConnectStart(ipAddr, port);
            long begin = System.currentTimeMillis();
            connectedSock.connect(socketAddress, 5000); // 连接60秒超时，不设置，则一直等下去
            long end = System.currentTimeMillis();
            d("连接耗时：" + (end - begin));
            notifyOnConnected(connectedSock, ipAddr, port);

        } catch (BindException e) {
            d(e.getMessage());
            error = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            d(e.getMessage());
            error = true;
        } catch (ConnectException e) {
            d(e.getMessage());
            error = true;
        } catch (SocketTimeoutException e) {
            d(e.getMessage());
            error = true;
        } catch (IOException e) {
            d(e.getMessage());
            error = true;
        } finally {
            if (error) {
                try {
                    d("error and close");
                    notifyOnConnectError(-1, "error", port);
                    connectedSock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopRun() {

    }

    private void notifyOnConnected(Socket socket, String server, int port) {
        if (listener != null)
            listener.onConnectOk(socket, server, port);
    }

    private void notifyOnConnectStart(String server, int port) {
        if (listener != null)
            listener.onConnectStart(server, port);
    }

    private void notifyOnConnectError(int code, String server, int port) {
        if (listener != null)
            listener.onConnectError(code, server, port);
    }

    private void d(String msg) {
        final String TAG = "TcpConnector";
        Log.w(TAG, msg);
    }
}
