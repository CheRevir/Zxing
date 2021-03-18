package com.cere.zxing.analyzer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.cere.zxing.ZxingConfig;
import com.google.zxing.Result;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by CheRevir on 2021/3/15
 */
public abstract class Analyzer implements ImageAnalysis.Analyzer, LifecycleObserver {
    private final ZxingConfig mConfig;
    private final AnalyzerResultCallback mResultCallback;
    private final ExecutorService mSingleThreadExecutor;

    public Analyzer(@NonNull ZxingConfig config, @NonNull AnalyzerResultCallback callback) {
        mConfig = config;
        mResultCallback = callback;
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @NonNull
    public ZxingConfig getConfig() {
        return mConfig;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        mSingleThreadExecutor.execute(() -> {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            int width = image.getWidth();
            int height = image.getHeight();
            mResultCallback.onAnalyzerResult(analyze(data, width, height, mConfig));
            image.close();
        });
    }

    @Nullable
    public abstract Result analyze(@NonNull byte[] data, int width, int height, @NonNull ZxingConfig config);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        mSingleThreadExecutor.shutdownNow();
    }
}
