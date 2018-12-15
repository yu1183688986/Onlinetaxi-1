package com.minicreate.online_taxi;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.minicreate.online_taxi.config.EndpointConfig;
import com.minicreate.online_taxi.entity.DaoMaster;
import com.minicreate.online_taxi.entity.DaoSession;
import com.minicreate.online_taxi.transmission.BusinessFlowManager;
import com.minicreate.online_taxi.transmission.TcpEndpoint;
import com.minicreate.online_taxi.transmission.UsbEndpoint;
import com.minicreate.online_taxi.transmission.listener.TcpConnectListenerAdapter;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.LicensePlateNumber_27;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Net1Param_22Hi3520DV300;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Signal4G_01;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.VehicleNo_20;
import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.protocol.beidou.SignInBeidou;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.PreferencesUtil;

import org.greenrobot.greendao.database.Database;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {
    private static final String TAG = "App";
    private DaoSession daoSession;
    private UsbEndpoint usbEndpoint;
    private Timer timer = new Timer();
    private Timer reConnTimer;//重连线程
    private volatile boolean isUsbConnecting = false;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate");
        super.onCreate();
        // regular SQLite database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "user-info-db");
        Database db = helper.getWritableDb();

        daoSession = new DaoMaster(db).newSession();
        //开启服务，监听App关闭
        Intent intent = new Intent(this, CheckExitService.class);
        //startService(intent);
        initUsbEndpoint(this);
    }

    /**
     * 初始化网络端点，连接到服务器
     */
    public static void initNetwork(Context context) {
        // 创建一个网络端点，连接到四个服务器
        EndpointConfig dcConfig = EndpointConfig.SERVER_DC;
        TcpEndpoint dcEndpoint = new TcpEndpoint(dcConfig.getIp(),
                dcConfig.getPort(),
                dcConfig.getStrategy(), context);
        dcEndpoint.startEndpoint(new TcpConnectListenerAdapter() {
            @Override
            public void onConnectOk(Socket socket, String server, int port) {
                //注册成功后，发送心跳包，首先得找出当前端点对应的BusinessFlow
                BusinessFlowManager.get(EndpointConfig.SERVER_DC.getIp(), context).sendHeartbeat();
            }
        });

        EndpointConfig config8081 = EndpointConfig.SERVER_TEST;
        TcpEndpoint endpoint8081 = new TcpEndpoint(config8081.getIp(),
                config8081.getPort(),
                config8081.getStrategy(), context);
        SignInBeidou signInBeidou = new SignInBeidou();
        signInBeidou.setCarID("000000079321");
        byte[] data = signInBeidou.parseToProtocol();
        LogUtil.d(TAG, "" + BytesUtil.BytestoHexStringPrintf(data));
//        endpoint8081.startEndpoint(new TcpConnectListenerAdapter() {
//            @Override
//            public void onConnectOk(Socket socket, String server, int port) {
//                LogUtil.d(TAG, "onConnectOk");
//                //北斗的服务器需要首先注册、然后才能干别的
//                //TODO 后续这些操作应该放在引导页中，或者是主页的第一项操作，连接期间弹出对话框“正在连接到服务器”，连接上之后再退出对话框
//                BusinessFlowManager.get(EndpointConfig.SERVER_TEST.getIp()).sendSetupCommand(new OnResponseListener() {
//                    @Override
//                    public void onResponse(Param result) {
//                        LogUtil.d(TAG, "onResponse result = " + result);
//                        if (result.isTimeOut()) {
//                            LogUtil.d(TAG, "协议超时 token = " + result.getToken());
//                        } else {
//                            //注册成功后，发送心跳包，首先得找出当前端点对应的BusinessFlow
//                            BusinessFlowManager.get(EndpointConfig.SERVER_TEST.getIp()).sendHeartbeat();
//                            LogUtil.d(TAG, "成功");
//                        }
//                    }
//                }, signInBeidou, data, signInBeidou.getToken());
//            }
//        });
    }

    public void releaseUsbEndpoint() {
        if (usbEndpoint != null) {
            usbEndpoint.closeEndpoint();
            usbEndpoint = null;
        }
    }

    /**
     * TODO 要小心，Application有可能会重启，那么里面的变量就会被清空，usbEndpoint也就变成了null。以后要加上判断为空时的重连操作
     *
     * @return
     */
    public UsbEndpoint getUsbEndpoint() {
        return usbEndpoint;
    }

    /**
     * 初始化USB连接端点
     */
    public void initUsbEndpoint(Context context) {
        LogUtil.d(TAG, "initUsbEndpoint usbEndpoint = " + usbEndpoint);
//        if (usbEndpoint != null) {
//            //重启usbEndpoint
//            //移除BusinessFlow
//            LogUtil.d(TAG, "BusinessFlowManager.remove(EndpointConfig.USB.getName());");
//            BusinessFlowManager.remove(EndpointConfig.USB.getName());
//            usbEndpoint.restartEndpoint();
//        } else {
        usbEndpoint = new UsbEndpoint(context,
                EndpointConfig.USB.getName(), EndpointConfig.USB.getStrategy());
        usbEndpoint.startEndpoint(new TcpConnectListenerAdapter() {
            @Override
            public void onConnectError(int code, String server, int port) {
                LogUtil.d(TAG, "onConnectError usbEndpoint = " + usbEndpoint);
                isUsbConnecting = false;
                //cancel了以后还要重新初始化，不然会报异常
                timer.cancel();
                timer = new Timer();
                //断开
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //延时10秒后重连
                            Thread.sleep(2 * 1000);
                            //双检锁
                            LogUtil.d(TAG, "1");
                            if (!isUsbConnecting) {
                                LogUtil.d(TAG, "2");
                                synchronized (App.this) {
                                    LogUtil.d(TAG, "3");
                                    if (!isUsbConnecting) {
                                        LogUtil.d(TAG, "4");
                                        isUsbConnecting = true;
                                        //加一个线程，不然锁无法释放
                                        usbEndpoint.restartEndpoint();
                                    }
                                }
                            }
                        } catch (
                                InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.

                        start();
            }

            @Override
            public void onConnectOk() {
                LogUtil.d(TAG, "initUsbEndpoint onConnectOk UsbEndpoint = " + usbEndpoint);
                //TODO 发送测试协议，5秒发送一次
                //先初始化业务类，然后发送协议
                Signal4G_01 signal4G_01 = new Signal4G_01(0x01, context);
                signal4G_01.setCommand(0x45);
                signal4G_01.setSignal(50);
                signal4G_01.setSocket1(1);
                signal4G_01.setSocket2(1);
                signal4G_01.setSocket3(1);
                signal4G_01.setSocket4(1);
                signal4G_01.setAcc(1);
                signal4G_01.setSd2State(1);

                byte[] data = signal4G_01.parseToProtocol();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        usbEndpoint.send(new OnResponseListener() {
                            @Override
                            public void onResponse(Param result) {
                                //网络信号强度是不会回应的
                            }
                        }, signal4G_01);
                    }
                };

                timer.schedule(task, 0, 5000);
                initAfterUsb(context);
                //连接成功之后，发送心跳
                usbEndpoint.sendHeartbeat();
            }
        });

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * USB连接后的一些需要发送的协议
     */
    private void initAfterUsb(Context context) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Net1Param_22Hi3520DV300 param = new Net1Param_22Hi3520DV300(0x22, context);
                    usbEndpoint.send(new OnResponseListener() {
                        @Override
                        public void onResponse(Param result) {
                            LogUtil.d(TAG, "onResponse result = " + result);
                            if (result.isTimeOut()) {
                                LogUtil.d(TAG, "协议超时 token = " + result.getToken());
                            } else {
                                LogUtil.d(TAG, "成功 token = " + result.getToken());
                                //成功之后，替换服务器，重启服务器
                                String ip = ((Net1Param_22Hi3520DV300) result).getIp1();
                                String port = ((Net1Param_22Hi3520DV300) result).getPort1();
                                //TODO 暂时屏蔽IP地址的获取
                                EndpointConfig.SERVER_DC.setIp(ip);
                                EndpointConfig.SERVER_DC.setPort(Integer.parseInt(port));
                                //TODO 这处地方需要修改，代码不够严谨，要顾虑到每一个服务器的重连情况
                                //连接服务器
                                //EndpointManager.get().getEndpointById(EndpointConfig.SERVER_DC.getName()).closeEndpoint();
                                initNetwork(context);
                            }
                        }
                    }, param);
                    /**
                     * 获取车辆编号
                     */
                    VehicleNo_20 vehicleNo_20 = new VehicleNo_20(context);
                    usbEndpoint.send(new OnResponseListener() {
                        @Override
                        public void onResponse(Param result) {
                            LogUtil.d(TAG, "onResponse result = " + result);
                            if (result.isTimeOut()) {
                                LogUtil.d(TAG, "协议超时 token = " + result.getToken());
                            } else {
                                LogUtil.d(TAG, "成功 token = " + result.getToken());
                                //成功之后，解析出车辆编号
                                LogUtil.d(TAG, "成功 getVehicleNo = " + ((VehicleNo_20) result).getVehicleNo());
                                PreferencesUtil.get(context, PreferencesUtil.NAME).put(PreferencesUtil.VehicleNo, ((VehicleNo_20) result).getVehicleNo());
                                //TODO 记得替换其他协议的车辆编号
                            }
                        }
                    }, vehicleNo_20);

                    /**
                     * 获取车牌号
                     */
                    LicensePlateNumber_27 licensePlateNumber_27 = new LicensePlateNumber_27(context);
                    usbEndpoint.send(new OnResponseListener() {
                        @Override
                        public void onResponse(Param result) {
                            LogUtil.d(TAG, "onResponse result = " + result);
                            if (result.isTimeOut()) {
                                LogUtil.d(TAG, "协议超时 token = " + result.getToken());
                            } else {
                                LogUtil.d(TAG, "成功 token = " + result.getToken() + " ,getLicensePlateNumber = " + ((LicensePlateNumber_27) result).getLicensePlateNumber());
                            }
                        }
                    }, licensePlateNumber_27);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
