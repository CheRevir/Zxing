package com.cere.zxing.app;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.cere.logc.LogC;
import com.cere.logc.LogConfig;
import com.cere.zxing.R;
import com.cere.zxing.ZxingConfig;
import com.cere.zxing.analyzer.Analyzer;
import com.cere.zxing.analyzer.AnalyzerResultCallback;
import com.cere.zxing.analyzer.MultiFormatAnalyzer;
import com.cere.zxing.view.ViewfinderView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

import java.util.concurrent.ExecutionException;

/**
 * Created by CheRevir on 2021/3/9
 */
public class CaptureActivity extends AppCompatActivity implements AnalyzerResultCallback, ResultPointCallback/*, ImageAnalysis.Analyzer*/ {
    private ListenableFuture<ProcessCameraProvider> mListenableFuture;
    private Preview mPreview;
    private ImageAnalysis mImageAnalysis;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        LogC.init(new LogConfig.Builder(this).setEnableSave(true).build());

        LogC.e("OnCreate");
        Log.e("TAG", "CaptureActivity -> onCreate: ");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreviewView previewView = findViewById(R.id.preview);
        mImageView = findViewById(R.id.iv);
        ViewfinderView viewfinderView = findViewById(R.id.view_finder);

        Rect rect = new Rect();
        getWindowManager().getDefaultDisplay().getRectSize(rect);
        LogC.e(rect);

        ZxingConfig config = new ZxingConfig(this)
                .setAnalyzerAreaRect(viewfinderView.getAreaRect())
                .setFullScreenArea(false);

        mPreview = new Preview.Builder()
                .setTargetResolution(new Size(rect.width(), rect.height()))
                .build();
        mPreview.setSurfaceProvider(previewView.getSurfaceProvider());

        mImageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(new Size(rect.width(), rect.height()))
                .build();
        Analyzer analyzer = new MultiFormatAnalyzer(config, this, bitmap -> {
            mImageView.post(() -> {
                LogC.e(bitmap);
                mImageView.setImageBitmap(bitmap);
            });
        });
        getLifecycle().addObserver(analyzer);
        mImageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), analyzer);
        mListenableFuture = ProcessCameraProvider.getInstance(this);
        mListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = mListenableFuture.get();
                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
                cameraProvider.bindToLifecycle(this, selector, mImageAnalysis, mPreview);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onAnalyzerResult(@Nullable Result result) {
        LogC.e(result);
        if (result != null) {
            mImageView.post(() -> {
                Toast.makeText(CaptureActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        LogC.e(point);
    }
}
