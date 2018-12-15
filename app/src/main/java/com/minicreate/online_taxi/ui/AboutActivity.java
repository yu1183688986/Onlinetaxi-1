package com.minicreate.online_taxi.ui;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.minicreate.online_taxi.R;
import com.minicreate.online_taxi.entity.AboutUsEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.list)
    RecyclerView mListView;

    private AboutUsAdapter adapter;
    private List<AboutUsEntity> aboutUsEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        refreshData();
        updateView();
    }

    private void refreshData() {
        addProperty("主板", Build.BOARD);
        addProperty("bootloader 版本", Build.BOOTLOADER);
        addProperty("android系统定制商", Build.BRAND);
        addProperty("型号", Build.MODEL);
        addProperty("系统版本", Build.VERSION.RELEASE);
        addProperty("软件版本", getAppVersionName(this));
    }

    private void updateView() {
        adapter = new AboutUsAdapter(aboutUsEntityList);
        mListView.setAdapter(adapter);
        //mListView.addItemDecoration(new SpaceItemDecoration(30));
        mListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mListView.setLayoutManager(new LinearLayoutManager(this));
    }

    //获取当前版本号
    private String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.minicreate.online_taxi", 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void addProperty(String name, String value) {
        AboutUsEntity temp = new AboutUsEntity();
        temp = new AboutUsEntity();
        temp.propertyName = name;
        temp.propertyValue = value;
        aboutUsEntityList.add(temp);
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            }

        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }
}
