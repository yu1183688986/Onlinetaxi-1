package com.minicreate.online_taxi.transmission;

import android.content.Context;

import com.minicreate.online_taxi.transmission.listener.TcpConnectListener;
import com.minicreate.online_taxi.transmission.listener.TcpEndpointInterface;
import com.minicreate.online_taxi.transmission.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;

public abstract class Endpoint {
    protected Context context;
    private String name;
    private ArrayList<byte[]> RequestData = new ArrayList<byte[]>();

    private List<TcpEndpointInterface> tcpEndpointLis = new ArrayList<TcpEndpointInterface>();
    protected Strategy strategy;

    protected final int CONNECT_INIT = 0;
    protected final int CONNECTING = 1;
    protected final int CONNECT_WAIT = 2;
    protected final int CONNECT_SUCCESS = 3;
    protected final int CONNECT_EXCEPTION = -1;
    /**
     * 端点的状态：0 初始；1 连接；2 重连接间歇；3 连接成功；-1 连接异常
     */
    protected int connectStatus = CONNECT_INIT;
    //	private boolean isAvailable = false;
//	private boolean isBusyConnecting = false;
    protected Boolean isEndpointValid = false;

    public Endpoint(String name, Strategy strategy, Context context) {
        this.name = name;
        this.strategy = strategy;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public void addReqData(byte[] data) {
        synchronized (RequestData) {
            //ResData.add(data);
            send(data, data.length);
        }
    }

    public void removeReqData(byte[] data) {
        synchronized (RequestData) {
            RequestData.remove(data);
        }
    }

    public void addEndpointListener(TcpEndpointInterface tcpEndLis) {
        synchronized (tcpEndpointLis) {
            if (tcpEndpointLis.contains(tcpEndLis)) {
                tcpEndpointLis.remove(tcpEndLis);
//				return;
            }
            tcpEndpointLis.add(tcpEndLis);
        }
    }

    public void removeEndpointListener(TcpEndpointInterface tcpEndLis) {
        synchronized (tcpEndpointLis) {
            tcpEndpointLis.remove(tcpEndLis);
        }
    }

    public void removeAllEndpointListener() {
        synchronized (tcpEndpointLis) {
            tcpEndpointLis.clear();
        }
    }

    public void notifyNetBroken() {
        if (tcpEndpointLis.size() == 0) {
        } else {
        }
        for (TcpEndpointInterface lis : tcpEndpointLis) {
            lis.onNetBroken();
        }
    }

    public void notifyEndpointStop() {
        for (TcpEndpointInterface lis : tcpEndpointLis) {
            lis.onEndpointStop();
        }
    }

    public void notifyConnSuccess() {
        if (tcpEndpointLis.size() == 0) {
        } else {
        }
        for (TcpEndpointInterface lis : tcpEndpointLis) {
            lis.onNetConnSuccess();
        }
    }

    public void notifyConnServerFailed() {
        for (TcpEndpointInterface lis : tcpEndpointLis) {
            lis.onConnServerFailed();
        }
    }

    public Strategy getProtocolStrategy() {
        return strategy;
    }

    public Boolean isEndpointValid() {
        return isEndpointValid;
    }

    public abstract void startEndpoint(TcpConnectListener... tcpConnectListener);

    /**
     * 停止端点的网络
     */
    public abstract void stopEndpoint();

    /**
     * 重启网络
     */
    public abstract void restartEndpoint();

    /**
     * 相比stop，多了清空与端点相关的 管理器、解析器、封装器、监听器
     */
    public abstract void closeEndpoint();

    protected abstract void endpointConnToNet();

    public abstract int send(byte[] buf, int length);

}
