package com.cere.zxing.analyzer;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.cere.logc.LogC;

/**
 * Created by CheRevir on 2021/3/12
 */
public class QRCodeAnalyzer implements ImageAnalysis.Analyzer {
    @Override
    public void analyze(@NonNull ImageProxy image) {
        LogC.e(image);
        image.close();
    }
}
