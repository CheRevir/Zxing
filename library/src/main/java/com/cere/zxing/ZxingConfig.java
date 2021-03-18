package com.cere.zxing;

import android.content.Context;
import android.graphics.Rect;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by CheRevir on 2021/3/4
 */
public class ZxingConfig {
    private Rect mAnalyzerAreaRect;
    private final Rect mScreenAreaRect = new Rect();
    private boolean isFullScreenArea = false;
    private boolean isSupportVertical = true;
    private boolean isSupportLuminanceInvert = true;

    public ZxingConfig(@NonNull Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getRectSize(mScreenAreaRect);
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

    @NonNull
    public static ZxingConfig getDefaultConfig(@NonNull Context context) {
        return new ZxingConfig(context);
    }
}
