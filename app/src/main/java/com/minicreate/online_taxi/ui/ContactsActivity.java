package com.minicreate.online_taxi.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.minicreate.online_taxi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactsActivity extends BaseActivity {
    @BindView(R.id.lv_contacts)
    RecyclerView mContactView;
    @BindView(R.id.btn_call_police)
    Button mCallPoliceButton;
    @BindView(R.id.btn_contact_us)
    Button mContactUsButton;

    @OnClick(R.id.btn_call_police)
    void callPolice() {
        callPhone("110");
    }

    @OnClick(R.id.btn_contact_us)
    void contactUs() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.bind(this);
        ContactsAdapter adapter = new ContactsAdapter();
        mContactView.setAdapter(adapter);
        mContactView.setLayoutManager(new LinearLayoutManager(this));
    }
}
