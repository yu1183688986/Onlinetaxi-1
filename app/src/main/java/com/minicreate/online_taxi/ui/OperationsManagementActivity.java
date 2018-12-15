package com.minicreate.online_taxi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.minicreate.online_taxi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 运维管理
 */
public class OperationsManagementActivity extends BaseActivity {
    @BindView(R.id.btn_parameter_setting)
    Button mParameterSettingBtn;
    Listener listener = new Listener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations_management);
        ButterKnife.bind(this);
        mParameterSettingBtn.setOnClickListener(listener);
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_parameter_setting: {
                    Intent intent = new Intent(OperationsManagementActivity.this, ParameterActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}
