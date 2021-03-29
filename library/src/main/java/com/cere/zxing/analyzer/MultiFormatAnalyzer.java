package com.cere.zxing.analyzer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cere.logc.LogC;
import com.cere.zxing.OnBitmapCallback;
import com.cere.zxing.ZxingConfig;
import com.cere.zxing.decode.DecodeFormatManager;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by CheRevir on 2021/3/15
 */
public class MultiFormatAnalyzer extends ImageAnalyzer {
    private final MultiFormatReader mMultiFormatReader;
    private final OnBitmapCallback mOnBitmapCallback;

    public MultiFormatAnalyzer(@NonNull ZxingConfig config, @NonNull AnalyzerResultCallback callback, OnBitmapCallback onBitmapCallback) {
        this(config, callback, DecodeFormatManager.QR_CODE_HINTS, onBitmapCallback);
    }

    public MultiFormatAnalyzer(@NonNull ZxingConfig config, @NonNull AnalyzerResultCallback callback, @NonNull Map<DecodeHintType, Object> hints, OnBitmapCallback onBitmapCallback) {
        super(config, callback);
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
        mOnBitmapCallback = onBitmapCallback;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Nullable
    @Override
    public Result analyze(@NonNull byte[] data, int dataWidth, int dataHeight, @NonNull Rect rect) {
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, dataWidth, dataHeight, rect.left, rect.top, rect.width(), rect.height(), false);

        Result result = decode(source);
        LogC.e(result);
        if (result == null && getConfig().isSupportVertical()) {
            byte[] rotatedData = rotate(data, dataWidth, dataHeight);
            result = decode(new PlanarYUVLuminanceSource(rotatedData, dataHeight, dataWidth, rect.top, rect.left, rect.height(), rect.width(), false));
        }
        LogC.e(result);
        if (result == null) {
            PlanarYUVLuminanceSource source1 = new PlanarYUVLuminanceSource(data, dataWidth, dataHeight, 0, 0, dataWidth, dataHeight, false);
            bitmap(source1);
            result = decode(source);
        }

        LogC.e(result);
        if (result == null && getConfig().isSupportLuminanceInvert()) {
            result = decode(source.invert());
        }
        return result;
    }

    private void bitmap(PlanarYUVLuminanceSource source) {
        try {
            byte[] bb = source.getMatrix();
            byte[] bytes = new byte[bb.length * 3];
            System.arraycopy(bb, 0, bytes, 0, bb.length);
            YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, source.getWidth(), source.getHeight(), null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 50, outputStream);
            byte[] b = outputStream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            mOnBitmapCallback.onBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "MultiFormatAnalyzer -> analyze: " + e);
        }
    }

    private Result decode(LuminanceSource source) {
        try {
            return mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            mMultiFormatReader.reset();
        }
        try {
            return mMultiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            mMultiFormatReader.reset();
        }
        return null;
    }
}
