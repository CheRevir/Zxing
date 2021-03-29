package com.cere.zxing.contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cere.zxing.app.CaptureActivity;

/**
 * Created by CheRevir on 2021/3/9
 */
public class ZxingResult extends ActivityResultContract<Integer, String> {
    public static final String ZXING_RESULT = "zxing_result";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Integer input) {
        return new Intent(context, CaptureActivity.class);
    }

    @Override
    public String parseResult(int resultCode, @Nullable Intent intent) {
        if (intent == null || resultCode != Activity.RESULT_OK) return null;
        return intent.getStringExtra(ZXING_RESULT);
    }
}
