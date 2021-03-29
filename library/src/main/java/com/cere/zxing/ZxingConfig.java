package com.cere.zxing;

import android.graphics.Rect;

import androidx.annotation.Nullable;

/**
 * Created by CheRevir on 2021/3/4
 */
public class ZxingConfig {
    private Rect mAnalyzerAreaRect;
    private Rect mScreenAreaRect;
    private boolean isFullScreenArea = false;
    private boolean isSupportVertical = true;
    private boolean isSupportLuminanceInvert = true;
    private boolean isVibrate = true;
    private boolean isSound = false;

    public ZxingConfig setSound(boolean sound) {
        isSound = sound;
        return this;
    }

    public boolean isSound() {
        return isSound;
    }

    public ZxingConfig setVibrate(boolean vibrate) {
        isVibrate = vibrate;
        return this;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public ZxingConfig setScreenAreaRect(Rect screenAreaRect) {
        mScreenAreaRect = screenAreaRect;
        return this;
    }

    public Rect getScreenAreaRect() {
        return mScreenAreaRect;
    }

    public ZxingConfig setSupportLuminanceInvert(boolean supportLuminanceInvert) {
        isSupportLuminanceInvert = supportLuminanceInvert;
        return this;
    }

    public boolean isSupportLuminanceInvert() {
        return isSupportLuminanceInvert;
    }

    public ZxingConfig setSupportVertical(boolean supportVertical) {
        isSupportVertical = supportVertical;
        return this;
    }

    public boolean isSupportVertical() {
        return isSupportVertical;
    }

    public ZxingConfig setFullScreenArea(boolean fullScreenArea) {
        isFullScreenArea = fullScreenArea;
        return this;
    }

    public boolean isFullScreenArea() {
        return isFullScreenArea;
    }

    public ZxingConfig setAnalyzerAreaRect(@Nullable Rect analyzerAreaRect) {
        mAnalyzerAreaRect = analyzerAreaRect;
        return this;
    }

    @Nullable
    public Rect getAnalyzerAreaRect() {
        return mAnalyzerAreaRect;
    }

    /*@NonNull
    public static ZxingConfig getDefaultConfig(@NonNull Context context) {
        return new ZxingConfig(context);
    }*/
}
