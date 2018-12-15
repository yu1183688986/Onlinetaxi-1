package com.minicreate.online_taxi.transmission.strategy;

import android.content.Context;

import com.minicreate.online_taxi.config.EndpointConfig;
import com.minicreate.online_taxi.event.Start3520DUpgradeEvent;
import com.minicreate.online_taxi.event.UpgradeResultEvent;
import com.minicreate.online_taxi.transmission.BusinessFlowManager;
import com.minicreate.online_taxi.transmission.EndpointManager;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.GPSHi3520DV300;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.HeartbeatHi3520DV300;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Upgrade_01;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Upgrade_02;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Upgrade_03;
import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.protocol.dianchuang.UnvarnishedTransmissionDianchuang;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;
import com.minicreate.online_taxi.utils.NetUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 与Hi3520DV300设备的交互协议策略类
 */
public class ProtocalHi3520DV300Strategy implements Strategy {
    //记录接收到的包序号
    private int currentPackage = 0;
    private static final String TAG = "ProtocalHi3520DV300Strategy";

    @Override
    public int getToken(Param param) {
        return param.getToken();
    }

    /**
     * 这一份协议的标识码就是子命令，也就是数组第16位
     * <p>
     * 记得先反转义
     *
     * @param src
     * @return
     */
    @Override
    public int getToken(byte[] src) {
        if (src.length < 16) {
            LogUtil.e(TAG, "数据有问题，长度太短");
            return -1;
        }
        return src[16];
    }

    /**
     * 转义
     *
     * @param unescapedBuffer
     * @return
     */
    @Override
    public byte[] escape(byte[] unescapedBuffer) {
        return unescapedBuffer;
    }

    @Override
    public byte[] unescape(byte[] escapedBuffer) {
        return NetUtil.unescape_0x7e_0x7d(escapedBuffer);
    }

    @Override
    public byte[] parseToProtocol(Param param) {
        return param.parseToProtocol();
    }

    @Override
    public Param parseFromProtocol(byte[] src, Param param) {
        param.parseFromProtocol(src);
        return param;
    }

    @Override
    public boolean checkData(byte[] src) {
        return true;
    }

    @Override
    public List<byte[]> findPackage(byte[] src) {
        return NetUtil.findPackageBySameHeadAndEnd(src, 0x7e);
    }

    @Override
    public Param getHeartbeat(Context context) {
        HeartbeatHi3520DV300 heartbeat = new HeartbeatHi3520DV300(context);
        return heartbeat;
    }

    @Override
    public long getHeartbeatPeriod() {
        return 3000;
    }

    @Override
    public boolean handleMessage(byte[] src, Context context) {
        if (src.length < 7) {
            LogUtil.e(TAG, "数据过短，小于7");
            return false;
        }
        int token = getToken(src);
        LogUtil.d(TAG, "命令字 = " + token + " ,src = " + BytesUtil.BytestoHexStringPrintf(src));
//        LogUtil.d(TAG, "token = " + token + " ,command = " + src[7]);
        //首先获取命令类型
        switch (src[7]) {
            case 0x45: {
                if (token == 0x02) {
                    //这个是GPS数据
                    GPSHi3520DV300 param = new GPSHi3520DV300(context);
                    param.parseFromProtocol(src);
//                    LogUtil.d(TAG, "gps = " + param.getGps());
                    return true;
                }
            }
            break;
            case 0x48: {
                //这是准备升级阶段，数据有可能是SC20发送到3520D的，也可能反过来
                if (token == 0x01) {
                    //判断回复码。回复码是升级协议（0x03）的内容，与命令类型、子命令是不同的
                    Upgrade_01 upgrade_01 = new Upgrade_01(context);
                    upgrade_01.parseFromProtocol(src);
                    LogUtil.d(TAG, "准备升级阶段");
                    if (upgrade_01.getNumber() == 0x01) {
                        //3520D端通知SC20升级，Apk、系统升级包、图片会分开发送
                        //准备升级包的目录，放在App的cache目录下，清空目录下的所有内容
                        LogUtil.d(TAG, "升级包目录 = " + context.getCacheDir());
                        for (File f : context.getCacheDir().listFiles()) {
                            f.delete();
                        }
                        currentPackage = 0;//包序号变回0
                        //准备好后立即发送0x03指令，告诉3520D可以发送升级包
                        upgrade_01.setNumber(0x03);
                        BusinessFlowManager.get(EndpointConfig.USB.getName(), context).sendAskForReprotCommand(upgrade_01.parseToProtocol());
                    } else if (upgrade_01.getNumber() == 0x02) {
                        //这个是3520D的回复
                        Start3520DUpgradeEvent event = new Start3520DUpgradeEvent();
                        event.result = 0x02;
                        EventBus.getDefault().post(event);
                    } else if (upgrade_01.getNumber() == 0x03) {
                        //升级端确认升级
                        LogUtil.d(TAG, "3520D开始升级");
                        Start3520DUpgradeEvent event = new Start3520DUpgradeEvent();
                        event.result = 0x03;
                        EventBus.getDefault().post(event);
                    } else {
                        //接收到了错误的回复码
                    }
                }
                //3520D发送升级包数据给SC20
                else if (token == 0x02) {
                    Upgrade_02 upgrade_02 = new Upgrade_02(context);
                    upgrade_02.parseFromProtocol(src);
                    LogUtil.d(TAG, "upgrade_02 = " + upgrade_02.toString());
                    //取出升级包名字
                    //文件所在目录
                    String fileName = context.getCacheDir().getAbsolutePath() + File.separator + upgrade_02.getFileName();
                    File file = new File(fileName);
                    try {
                        if (file.exists()) {
                            //文件存在，那么继续写入文件
                            FileOutputStream out = new FileOutputStream(file, true);
                            out.write(upgrade_02.getFileData());
                            out.close();
                        } else {
                            //文件不存在，创建并写入
                            file.createNewFile();
                            FileOutputStream out = new FileOutputStream(file, true);
                            out.write(upgrade_02.getFileData());
                            out.close();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentPackage++;
                    //判断包序号，看看有没有丢包
                    if (upgrade_02.getCurrentPackage() > currentPackage) {
                        //丢包了，通知3520D
                        Upgrade_03 upgrade_03 = new Upgrade_03(context);
                        upgrade_03.setFileName(upgrade_02.getFileName());
                        upgrade_03.setFileNameLength(upgrade_02.getFileName().length());
                        upgrade_03.setCurrentPackage(currentPackage++);
                        upgrade_03.setState(0);
                        BusinessFlowManager.get(EndpointConfig.USB.getName(), context).sendSetupCommand(new OnResponseListener() {
                            @Override
                            public void onResponse(Param result) {

                            }
                        }, upgrade_03, upgrade_03.parseToProtocol(), upgrade_03.getToken());
                    }

                    //判断升级包是否接收完成，完成了就发送升级成功协议给3520D
                    if (currentPackage == upgrade_02.getTotalPackage()) {
                        //发送完毕了
                        Upgrade_03 upgrade_03 = new Upgrade_03(context);
                        upgrade_03.setFileNameLength(upgrade_02.getFileName().length());
                        upgrade_03.setFileName(upgrade_02.getFileName());
                        upgrade_03.setCurrentPackage(0);
                        upgrade_03.setState(1);
                        BusinessFlowManager.get(EndpointConfig.USB.getName(), context).sendSetupCommand(new OnResponseListener() {
                            @Override
                            public void onResponse(Param result) {

                            }
                        }, upgrade_03, upgrade_03.parseToProtocol(), upgrade_03.getToken());
                    }
                }
                //升级包回复
                else if (token == 0x03) {
                    Upgrade_03 upgrade_03 = new Upgrade_03(context);
                    upgrade_03.parseFromProtocol(src);
                    LogUtil.d(TAG, "currentPackage = " + upgrade_03.getCurrentPackage() + " ,state = " + upgrade_03.getState());

                    UpgradeResultEvent resultEvent = new UpgradeResultEvent();
                    resultEvent.currentPackage = upgrade_03.getCurrentPackage();
                    resultEvent.state = upgrade_03.getState();
                    EventBus.getDefault().post(resultEvent);
                }
                return true;
            }
            case 0x50: {
                //首先拆包
                //获取内容长度，第14位是高位，第15位是低位
                int height = (src[14] & 0xff) << 8;
                int low = src[15] & 0xff;
                int contentLen = height + low;
                byte[] zhuanfaBuf = new byte[contentLen];
                UnvarnishedTransmissionDianchuang param = new UnvarnishedTransmissionDianchuang(0);//填什么一样，其实不需要参数
                System.arraycopy(src, 16, zhuanfaBuf, 0, zhuanfaBuf.length);
                param.setData(zhuanfaBuf);
                LogUtil.d(TAG, "zhuanfaBuf = " + BytesUtil.BytestoHexStringPrintf(zhuanfaBuf));
                LogUtil.d(TAG, "zhuanfa send Buf = " + BytesUtil.BytestoHexStringPrintf(param.parseToProtocol()));
                LogUtil.d("hjw_test", "zhuanfa send Buf = " + BytesUtil.BytestoHexStringPrintf(param.parseToProtocol()));
                //转发
                //首先需要判断服务器是否连接上了
                if (EndpointManager.get().getEndpointById(EndpointConfig.SERVER_DC.getIp()) != null) {
                    BusinessFlowManager.get(EndpointConfig.SERVER_DC.getIp(), context).sendSetupCommand(new OnResponseListener() {
                        @Override
                        public void onResponse(Param result) {
                            LogUtil.d(TAG, "onResponse");
                        }
                    }, param);
                } else {
                    //公司的服务器没连接上
                    LogUtil.e(TAG, "点创科技的服务器没连接上");
                }
                //返回true，后续不需要处理
                return true;
            }
        }
        return false;
    }
}
