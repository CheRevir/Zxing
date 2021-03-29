package com.cere.zxing.app;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.cere.zxing.R;
import com.cere.zxing.ZxingConfig;

/**
 * Created by CheRevir on 2021/3/19
 */
public class BeepManager implements LifecycleObserver {
    private final ZxingConfig mConfig;
    private final Vibrator mVibrator;
    private final SoundPool mSoundPool;
    private final int mSoundId;

    public BeepManager(Context context, ZxingConfig config, LifecycleOwner owner) {
        this.mConfig = config;
        owner.getLifecycle().addObserver(this);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
                .build();
        mSoundId = mSoundPool.load(context, R.raw.beep, 1);
    }

    public void start() {
        if (mConfig.isVibrate() && mVibrator.hasVibrator()) {
            mVibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        if (mConfig.isSound() && mSoundId != 0) {
            mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void release() {
        mSoundPool.release();
    }
}
