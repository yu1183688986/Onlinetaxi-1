package com.minicreate.online_taxi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.minicreate.online_taxi.R;
import com.minicreate.online_taxi.config.EndpointConfig;
import com.minicreate.online_taxi.event.Start3520DUpgradeEvent;
import com.minicreate.online_taxi.event.UpgradeResultEvent;
import com.minicreate.online_taxi.transmission.BusinessFlowManager;
import com.minicreate.online_taxi.transmission.Endpoint;
import com.minicreate.online_taxi.transmission.EndpointManager;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Upgrade_01;
import com.minicreate.online_taxi.transmission.protocol.Hi3520DV300.Upgrade_02;
import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.utils.BytesUtil;
import com.minicreate.online_taxi.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends BaseActivity {
    private static final String TAG = "TestActivity";
    @BindView(R.id.btn_upgrade)
    Button mUpgradeBtn;
    long seek = 0;
    private volatile boolean upgradeError = false;

    private Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        listener = new Listener();
        mUpgradeBtn.setOnClickListener(listener);
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_upgrade: {
                    sendStartUpgrade();
                }
                break;
            }
        }
    }

    /**
     * 发送升级指令
     */
    private void sendStartUpgrade() {
        LogUtil.d(TAG, "sendStartUpgrade");
        //告诉3520D准备开始升级
        Upgrade_01 upgrade_01 = new Upgrade_01(TestActivity.this);
        upgrade_01.setNumber(0x01);
        BusinessFlowManager.get(EndpointConfig.USB.getName(), TestActivity.this).sendSetupCommand(new OnResponseListener() {
            @Override
            public void onResponse(Param result) {
                //不需要处理回应，3520D收到之后，会发送升级确认，只要收到就升级
            }
        }, upgrade_01);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeResult(UpgradeResultEvent event) {
        LogUtil.d(TAG, "onUpgradeResult event = " + event.toString());
        if (event.state == 1) {
            //升级成功
            upgradeError = false;
        } else {
            //升级失败
            upgradeError = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartUpgrade(Start3520DUpgradeEvent event) {
        LogUtil.d(TAG, "onStartUpgrade event.result = " + event.result);
        Toast.makeText(TestActivity.this, "3520D已收到指令，等待升级", Toast.LENGTH_SHORT).show();
        //收到了异步的升级指令，立即开始升级
        if (event.result == 0x03) {
            new Thread() {
                @Override
                public void run() {
                    sendUpgradeFile("/sdcard/123");
                }
            }.start();
        }
    }

    /**
     * 发送升级文件
     */
    private void sendUpgradeFile(String fileName) {
        File file = new File(fileName);
        LogUtil.e(TAG, "升级文件 = " + file.getAbsolutePath() + " ,大小 = " + file.length());
        RandomAccessFile randomAccessFile = null;
        Endpoint endpoint = EndpointManager.get().getEndpointById(EndpointConfig.USB.getName());
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            int currentPackage = 1;
            //先读取第一包
            //upgradeError用来判断3520D升级是否出错
            //初始化协议类
            Upgrade_02 upgrade_02 = new Upgrade_02(TestActivity.this);
            upgrade_02.setDirection(0x01);
            upgrade_02.setFileName(file.getName());
            upgrade_02.setFileSize(file.length());
            upgrade_02.setFileType(1);
            int total = 0;
            if (file.length() % (Upgrade_02.BYTE_LEN) == 0) {
                total = (int) (file.length() / (Upgrade_02.BYTE_LEN));
            } else {
                total = (int) (file.length() / (Upgrade_02.BYTE_LEN) + 1);
            }
            upgrade_02.setTotalPackage(total);
            while ((seek < file.length()) && !upgradeError) {
                byte[] tmp = getFilePackage(randomAccessFile, file, seek);
                //打包数据
                upgrade_02.setCurrentPackageSize(tmp.length);
                upgrade_02.setFileData(tmp);
                upgrade_02.setCurrentPackage(currentPackage);
                seek += tmp.length;
                LogUtil.d(TAG, "tmp.length = " + tmp.length + " ,seek = " + seek + " ,currentPackage = " + currentPackage + " ,total = " + total);
//                LogUtil.d(TAG, "data = " + BytesUtil.BytestoHexStringPrintf(upgrade_02.parseToProtocol()));
                //发送数据
//                BusinessFlowManager.get(EndpointConfig.USB.getName(), TestActivity.this).sendAskForReprotCommand(upgrade_02.parseToProtocol());
                byte[] sendData = upgrade_02.parseToProtocol();
                endpoint.send(sendData, sendData.length);
                currentPackage++;
            }

            if (upgradeError) {
                //升级出错
                LogUtil.d(TAG, "升级出错");
                //重新发送升级指令
                //TODO 新增一个升级出错次数判断，如果升级失败超过3次，那么就不要升级了，报告失败消息给服务器
                new Thread() {
                    @Override
                    public void run() {
                        //TODO 暂时注释掉，为了收集log
//                        sendStartUpgrade();
                        //重置upgradeError
                        upgradeError = false;
                    }
                }.start();
            } else {
                LogUtil.d(TAG, "发送完毕");
            }

            randomAccessFile.close();
        } catch (FileNotFoundException e1) {
            LogUtil.e(TAG, "FileNotFoundException e1 = " + e1);
            e1.printStackTrace();
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "IOException e = " + e);
            e.printStackTrace();
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 根据偏移获取其中一包数据
     *
     * @param randomAccessFile
     * @param file
     * @param seek
     * @return
     * @throws IOException
     */
    private byte[] getFilePackage(RandomAccessFile randomAccessFile, File file, long seek) throws IOException {
        byte[] tmp;
        if (seek + (Upgrade_02.BYTE_LEN) >= file.length()) {
            tmp = new byte[(int) (file.length() - seek)];
        } else {
            tmp = new byte[Upgrade_02.BYTE_LEN];
        }
        randomAccessFile.readFully(tmp, 0, tmp.length);
        return tmp;
    }
}
