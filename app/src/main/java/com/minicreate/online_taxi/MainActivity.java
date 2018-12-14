package com.minicreate.online_taxi;

import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.btn_navigation)
    Button navigationBtn;
    @BindView(R.id.btn_didi)
    Button didiBtn;

    @OnClick(R.id.btn_navigation)
    void navigation() {
        startApp("com.autonavi.amapauto");
    }

    @OnClick(R.id.btn_didi)
    void didi() {
        startApp("com.sdu.didi.gui");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
}
