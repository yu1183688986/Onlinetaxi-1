package com.minicreate.online_taxi.ui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

import com.minicreate.online_taxi.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RingActivity extends BaseActivity {
    @BindView(R.id.seek_ring_volume)
    SeekBar mRingSeek;
    @BindView(R.id.seek_media_volume)
    SeekBar mMediaSeek;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        ButterKnife.bind(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mRingSeek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        mRingSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMediaSeek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mMediaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
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
