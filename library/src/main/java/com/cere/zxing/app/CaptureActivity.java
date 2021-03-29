package com.cere.zxing.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.cere.zxing.contracts.ZxingResult;
import com.cere.zxing.view.ViewfinderView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.Result;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

/**
 * Created by CheRevir on 2021/3/9
 */
public class CaptureActivity extends AppCompatActivity implements AnalyzerResultCallback, ActivityResultCallback<Uri> {
    private ListenableFuture<ProcessCameraProvider> mListenableFuture;
    private Preview mPreview;
    private ImageAnalysis mImageAnalysis;
    private ImageButton ib_flashlight;
    private BeepManager mBeepManager;
    private ImageView mImageView;

    private final ActivityResultLauncher<String> mGetContentUri = registerForActivityResult(new ActivityResultContracts.GetContent(), this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        LogC.init(new LogConfig.Builder(this).setEnableSave(true).build());

        LogC.e(2 >> 4);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreviewView previewView = findViewById(R.id.preview);
        ib_flashlight = findViewById(R.id.activity_capture_ib_flashlight);
        findViewById(R.id.activity_capture_flashlight).setOnClickListener(view -> {

        });
        findViewById(R.id.activity_capture_photo).setOnClickListener(view -> {
            mGetContentUri.launch("image/*");
        });

        mImageView = findViewById(R.id.iv);
        ViewfinderView viewfinderView = findViewById(R.id.view_finder);

        Rect rect = new Rect();
        getWindowManager().getDefaultDisplay().getRectSize(rect);

        LogC.e(rect);

        ZxingConfig config = new ZxingConfig()
                .setScreenAreaRect(viewfinderView.getRect())
                .setAnalyzerAreaRect(viewfinderView.getAreaRect())
                .setSound(true)
                .setFullScreenArea(false);

        mBeepManager = new BeepManager(this, config, this);

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
                // LogC.e(bitmap);
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
        //LogC.e(result);
        if (result != null) {
            ib_flashlight.post(() -> {
                mImageAnalysis.clearAnalyzer();
                mBeepManager.start();
                setResult(RESULT_OK, new Intent().putExtra(ZxingResult.ZXING_RESULT, result.getText()));
                finish();
            });
            mImageView.post(() -> {
                Toast.makeText(CaptureActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onActivityResult(Uri result) {
        if (result == null) return;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(result));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
