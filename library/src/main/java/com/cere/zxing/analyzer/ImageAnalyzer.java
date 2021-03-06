package com.cere.zxing.analyzer;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cere.zxing.ZxingConfig;
import com.google.zxing.Result;

/**
 * Created by CheRevir on 2021/3/15
 */
public abstract class ImageAnalyzer extends Analyzer {
    public ImageAnalyzer(@NonNull ZxingConfig config, @NonNull AnalyzerResultCallback callback) {
        super(config, callback);
    }

    @Nullable
    @Override
    public Result analyze(@NonNull byte[] data, int width, int height, @NonNull ZxingConfig config) {
        if (!config.isFullScreenArea()) {
            Rect rect = config.getAnalyzerAreaRect();
            if (rect != null) {
                return analyze(data, width, height, rect, config);
            }
        }
        return analyze(data, width, height, new Rect(0, 0, width, height), config);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Result analyze(byte[] data, int dataWidth, int dataHeight, Rect rect, ZxingConfig config) {
        Rect rotateRect = new Rect(rect);
        Rect screenAreaRect = config.getScreenAreaRect();
        if (screenAreaRect != null && !config.isFullScreenArea()) {
            float scaleWidth = dataHeight / (float) screenAreaRect.width();
            float scaleHeight = dataWidth / (float) screenAreaRect.height();
            rotateRect.left = (int) (rotateRect.left * scaleWidth);
            rotateRect.right = (int) (rotateRect.right * scaleWidth);
            rotateRect.top = (int) (rotateRect.top * scaleHeight);
            rotateRect.bottom = (int) (rotateRect.bottom * scaleHeight);
        } else {
            rotateRect.set(0, 0, dataWidth, dataHeight);
        }
        return analyze(rotate(data, dataWidth, dataHeight), dataHeight, dataWidth, rotateRect);
    }

    @Nullable
    public abstract Result analyze(@NonNull byte[] data, int dataWidth, int dataHeight, @NonNull Rect rect);

    @NonNull
    protected byte[] rotate(@NonNull byte[] data, int width, int height) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        return rotatedData;
    }
}
