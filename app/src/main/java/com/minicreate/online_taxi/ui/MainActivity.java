package com.minicreate.online_taxi.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.minicreate.online_taxi.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.btn_navigation)
    Button navigationBtn;
    @BindView(R.id.btn_didi)
    Button didiBtn;
    @BindView(R.id.iv_user_profile)
    ImageView mUserProfileImageView;
    @BindView(R.id.btn_phone)
    Button mPhoneButton;
    @BindView(R.id.btn_main_msg)
    Button mMessageButton;
    @BindView(R.id.btn_settings)
    Button mSettingsButton;

    ClickListener listener = new ClickListener();

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    String[] permissions = {Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_SETTINGS,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigationBtn.setOnClickListener(listener);
        didiBtn.setOnClickListener(listener);
        mUserProfileImageView.setOnClickListener(listener);
        mPhoneButton.setOnClickListener(listener);
        mMessageButton.setOnClickListener(listener);
        mSettingsButton.setOnClickListener(listener);
        checkAllPermission();
    }

    /**
     * 判断该app所有的权限是否都被注册了
     */
    private void checkAllPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限，MY_PERMISSIONS_REQUEST_READ_CONTACTS是申请权限的请求id，是整型值，自己随便取就行了，系统申请权限之后，不管成功与否，都会回调通知我们，回调中同时传递过来一个id，我们可以用这个新的id来与前面发送的id对比就能知道这个请求是不是我们发出的。
            requestPermissions(permissions, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //有权限的话就走这一步
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult requestCode = " + requestCode + " ,permissions = " + Arrays.toString(permissions) + " ,grantResults = " + Arrays.toString(grantResults));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            //检查一下requestCode是否是我们发出的
            if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
                //grantResults数组保存了权限申请成功与否，因为我们只请求了一个权限，所以只看第一个就行了
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + "申请到了权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, permissions[i] + "权限申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_navigation: {
                    startApp("com.autonavi.amapauto");
                    break;
                }
                case R.id.btn_didi: {
                    startApp("com.sdu.didi.gui");
                    break;
                }
                case R.id.iv_user_profile: {
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btn_phone: {
                    Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btn_main_msg: {
                    Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btn_settings: {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                }
                default: {

                }
            }
        }
    }
}
