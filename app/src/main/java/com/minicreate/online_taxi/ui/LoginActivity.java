package com.minicreate.online_taxi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.minicreate.online_taxi.App;
import com.minicreate.online_taxi.R;
import com.minicreate.online_taxi.config.EndpointConfig;
import com.minicreate.online_taxi.entity.DaoSession;
import com.minicreate.online_taxi.entity.UserInfoEntity;
import com.minicreate.online_taxi.entity.UserInfoEntityDao;
import com.minicreate.online_taxi.transmission.BusinessFlowManager;
import com.minicreate.online_taxi.transmission.protocol.OnResponseListener;
import com.minicreate.online_taxi.transmission.protocol.Param;
import com.minicreate.online_taxi.transmission.protocol.beidou.Logout;
import com.minicreate.online_taxi.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.btn_login)
    Button mLoginBtn;
    @BindView(R.id.btn_login_history)
    Button mHistoryBtn;
    @BindView(R.id.et_login_user_id)
    EditText mUserIdText;
    @BindView(R.id.txt_password_hint)
    TextView mPasswordhint;
    @BindView(R.id.btn_test)
    Button mTestBtn;

    private Listener listener;
    private UserInfoEntityDao userDao;

    @OnClick(R.id.btn_login)
    void login() {
        String userId = mUserIdText.getText().toString().trim();
        if (!TextUtils.isEmpty(userId)) {
            if (userId.length() > 20) {
                //工号太长了
                mPasswordhint.setText(getString(R.string.str_login_user_id_length_error));
                mPasswordhint.setVisibility(View.VISIBLE);
            } else {
                UserInfoEntity entity = new UserInfoEntity();
                entity.setId(userId);
                //TODO 暂时先设置一个默认的姓名，以后有了协议后，再修改
                entity.setName("测试用户");
                entity.setOperation("签到时间");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                entity.setSignInTime(dateFormat.format(new Date()));
                saveLoginInfo(entity);
                mPasswordhint.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            //工号为空
            mPasswordhint.setText(getString(R.string.str_login_user_id_length_error));
            mPasswordhint.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_login_history)
    void loginHistory() {
        Intent intent = new Intent(this, LoginHistoryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        listener = new Listener();
        // get the note DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        userDao = daoSession.getUserInfoEntityDao();
        //TODO 记得写一个全局的网络监听器，随时监听网络改变

        mTestBtn.setOnClickListener(listener);
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_test: {
                    Intent intent = new Intent(LoginActivity.this, TestActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    /**
     * 保存登录信息到数据库
     */
    private void saveLoginInfo(UserInfoEntity entity) {
        userDao.insert(entity);
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
//        Logout logout = new Logout();
//        logout.setCarID("000000079321");
//        byte[] data = logout.parseToProtocol();
//        BusinessFlowManager.get(EndpointConfig.SERVER_TEST.getIp(), getApplicationContext()).sendSetupCommand(new OnResponseListener() {
//            @Override
//            public void onResponse(Param result) {
//                LogUtil.d(TAG, "onResponse");
//            }
//        }, logout, data, logout.getToken());
        super.onDestroy();
    }
}
