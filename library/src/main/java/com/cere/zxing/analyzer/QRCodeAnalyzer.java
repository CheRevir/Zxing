package com.cere.zxing.analyzer;

import androidx.annotation.NonNull;

import com.cere.zxing.OnBitmapCallback;
import com.cere.zxing.ZxingConfig;
import com.google.zxing.DecodeHintType;

import java.util.Map;

/**
 * Created by CheRevir on 2021/3/12
 */
public class QRCodeAnalyzer extends MultiFormatAnalyzer {

    public QRCodeAnalyzer(@NonNull ZxingConfig config, @NonNull AnalyzerResultCallback callback, @NonNull Map<DecodeHintType, Object> hints, OnBitmapCallback onBitmapCallback) {
        super(config, callback, hints, onBitmapCallback);
    }
}
