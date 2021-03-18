package com.cere.zxing.analyzer;

import androidx.annotation.Nullable;

import com.google.zxing.Result;

/**
 * Created by CheRevir on 2021/3/15
 */
public interface AnalyzerResultCallback {
    void onAnalyzerResult(@Nullable Result result);
}
