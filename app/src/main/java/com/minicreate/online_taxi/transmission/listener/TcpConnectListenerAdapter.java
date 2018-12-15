package com.minicreate.online_taxi.transmission.listener;

import java.net.Socket;

public class TcpConnectListenerAdapter implements TcpConnectListener {
    @Override
    public void onConnectStart(String server, int port) {

    }

    @Override
    public void onConnectOk(Socket socket, String server, int port) {

    }

    @Override
    public void onConnectOk() {

    }

    @Override
    public void onConnectError(int code, String server, int port) {

    }
}
