package com.minicreate.online_taxi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.minicreate.online_taxi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BacklightActivity extends BaseActivity {
    @BindView(R.id.seek_back_light)
    SeekBar mBackLightBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlight);
        ButterKnife.bind(this);
        mBackLightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                android.provider.Settings.System.putInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
