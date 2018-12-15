package com.minicreate.online_taxi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.minicreate.online_taxi.App;
import com.minicreate.online_taxi.R;
import com.minicreate.online_taxi.entity.DaoSession;
import com.minicreate.online_taxi.entity.UserInfoEntity;
import com.minicreate.online_taxi.entity.UserInfoEntityDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginHistoryActivity extends Activity {
    @BindView(R.id.rv_history)
    public RecyclerView mHistoryView;

    private Query<UserInfoEntity> userQuery;
    private UserInfoEntityDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        LoginHistoryAdapter adapter = new LoginHistoryAdapter();
        mHistoryView.setAdapter(adapter);
        mHistoryView.setLayoutManager(new LinearLayoutManager(this));

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        userDao = daoSession.getUserInfoEntityDao();
        userQuery = userDao.queryBuilder().orderAsc(UserInfoEntityDao.Properties.Id).build();
        List<UserInfoEntity> userInfos = userQuery.list();
        adapter.setUserInfos(userInfos);
    }

    private void updateView() {

    }
}
