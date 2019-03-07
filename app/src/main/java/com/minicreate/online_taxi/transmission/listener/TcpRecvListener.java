package com.minicreate.online_taxi.transmission.listener;

public interface TcpRecvListener {
    public void onReceive(byte[] buf, int len);

    public void onReceiveBegin();

    public void onReceiveEnd();

    public void onRecvNetException();

    public String getTcpRecvListenerName();
}
