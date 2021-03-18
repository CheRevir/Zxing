package com.cere.zxing.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cere.zxing.contracts.ZxingResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        ActivityResultLauncher<Integer> launcher = registerForActivityResult(new ZxingResult(), result -> {
            Log.e("TAG", "MainActivity -> : Result: " + result);
        });

        findViewById(R.id.bt).setOnClickListener((view) -> {
            launcher.launch(0);
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "MainActivity -> onConfigurationChanged: " + newConfig);
    }

    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("TAG", "MainActivity -> onRequestPermissionsResult: " + requestCode);
        Log.e("TAG", "MainActivity -> onRequestPermissionsResult: " + Arrays.toString(permissions));
        Log.e("TAG", "MainActivity -> onRequestPermissionsResult: " + Arrays.toString(grantResults));
    }
}