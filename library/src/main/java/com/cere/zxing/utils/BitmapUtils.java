package com.cere.zxing.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;

/**
 * Created by CheRevir on 2021/3/4
 */
public class BitmapUtils {

    public static Bitmap getBitmapFromUri(Context context, Uri uri, int width, int height) {
        try {
            ParcelFileDescriptor descriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(descriptor.getFileDescriptor());
            if (bitmap != null) {
                return scaleBitmap(bitmap, width, height);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float sw = (float) width / w;
            float sh = (float) height / h;
            matrix.setScale(sw, sh);
            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            if (!bmp.equals(bitmap) && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return bmp;
        }
        return null;
    }
}
