package com.minicreate.online_taxi.transmission;

import android.content.Context;
import android.util.Log;

import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import java.util.LinkedList;
import java.util.List;

public class TCPDispatcher extends Thread {
    private Context context;
    private String name = "";
    private final String TAG = "TCPDispatcher";
    private List<byte[]> recvQueue = new LinkedList<byte[]>();
//	private Extractor1 extractor;
//	private Extractor1.ServerRespData resClass1;

    public TCPDispatcher(String name, Context context) {
        this.context = context;
//		extractor = new Extractor1();
        d("TCPDispatcher 构造函数。。。。。。。。。。。。");
        this.name = name;
    }

    public void addRespData(byte[] data) {
        synchronized (recvQueue) {
            recvQueue.add(data);
        }
    }

    private void removeRespData(byte[] data) {
        synchronized (recvQueue) {
            recvQueue.remove(data);
        }
    }

    public void notifyParserLib(String funID, Object data) {
//		ParserLib.get().setData(funID, data);
    }

    @Override
    public void run() {
        d("TCPDispatcher run..." + Thread.currentThread().getId());
        while (!isInterrupted()) {
            if (recvQueue.size() > 0) {
                byte[] data = recvQueue.get(0);
                LogUtil.v(TAG, "Recv : " + BytesUtil.BytestoHexStringPrintf(data));
                interpretResponse(data);
                removeRespData(recvQueue.get(0));
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        d("TCPDispatcher thread over");
    }

    public void stopRun() {
        d("stopRun Dispatcher Thread");
        //close();
        interrupt();
    }

    private void d(String msg) {
        Log.w(TAG, msg);
    }

    private void interpretResponse(byte[] message) {
        BusinessFlowManager.get(name, context).responseBusiness(message);
    }
}
