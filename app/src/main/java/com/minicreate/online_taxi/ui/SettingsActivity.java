package com.minicreate.online_taxi.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.minicreate.online_taxi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.btn_ring_setting)
    Button mRingBtn;
    @BindView(R.id.btn_wifi_setting)
    Button mWifiBtn;
    @BindView(R.id.btn_back_light_setting)
    Button mBackLightBtn;
    @BindView(R.id.btn_operations_management)
    Button mOperationsManagementBtn;
    @BindView(R.id.btn_about_us)
    Button mAboutUsBtn;

    ClickListener listener = new ClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mRingBtn.setOnClickListener(listener);
        mWifiBtn.setOnClickListener(listener);
        mBackLightBtn.setOnClickListener(listener);
        mOperationsManagementBtn.setOnClickListener(listener);
        mAboutUsBtn.setOnClickListener(listener);
    }

    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_ring_setting: {
                    Intent intent = new Intent(SettingsActivity.this, RingActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btn_wifi_setting: {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$WifiSettingsActivity"));
                    startActivity(intent);
                    break;
                }
                case R.id.btn_back_light_setting: {
                    Intent intent = new Intent(SettingsActivity.this, BacklightActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btn_operations_management: {
                    Intent intent = new Intent(SettingsActivity.this, OperationsManagementActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btn_about_us: {
                    Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}
