package com.cere.zxing.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.cere.zxing.R;

/**
 * Created by CheRevir on 2021/3/9
 */
public class ViewfinderView extends View {
    private final Rect frame;
    private int maskColor;
    private int reactColor;
    private int boundColor;
    private int scanLineColor;
    private Paint maskPaint;
    private Paint reactPaint;
    private Paint boundPaint;
    private Paint scanLinePaint;

    private int scanLinePosition;
    private ValueAnimator mValueAnimator;

    public ViewfinderView(Context context) {
        this(context, null);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView, defStyleAttr, defStyleRes);
        maskColor = typedArray.getColor(R.styleable.ViewfinderView_maskColor, context.getColor(R.color.viewfinder_mask));
        boundColor = typedArray.getColor(R.styleable.ViewfinderView_boundColor, Color.WHITE);
        reactColor = typedArray.getColor(R.styleable.ViewfinderView_reactColor, Color.WHITE);
        scanLineColor = typedArray.getColor(R.styleable.ViewfinderView_scanLineColor, Color.WHITE);
        typedArray.recycle();
        frame = getFrame(context);
        initPoint();
        initAnimator(frame.top, frame.bottom);
    }

    private void initPoint() {
        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setColor(maskColor);

        boundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boundPaint.setColor(boundColor);
        boundPaint.setStyle(Paint.Style.STROKE);
        boundPaint.setStrokeWidth(dp2px(1));

        reactPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reactPaint.setColor(reactColor);
        reactPaint.setStyle(Paint.Style.FILL);
        reactPaint.setStrokeWidth(dp2px(1));

        scanLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scanLinePaint.setColor(scanLineColor);
        scanLinePaint.setDither(true);
        scanLinePaint.setStyle(Paint.Style.FILL);
        scanLinePaint.setStrokeWidth(dp2px(2));
    }

    private void initAnimator(int start, int stop) {
        mValueAnimator = ValueAnimator.ofInt(start, stop);
        mValueAnimator.setDuration(3000);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(animation -> {
            scanLinePosition = (int) animation.getAnimatedValue();
            invalidate();
        });
        mValueAnimator.start();
    }

    public void stop() {
        if (mValueAnimator != null) {
            mValueAnimator.end();
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMaskView(canvas, frame, maskPaint);
        drawFrameBoundsView(canvas, frame, boundPaint, reactPaint);
        drawScanLineView(canvas, frame, scanLinePosition, scanLinePaint);
    }

    public Rect getAreaRect() {
        return frame;
    }

    public void setBoundColor(@ColorInt int boundColor) {
        this.boundColor = boundColor;
    }

    public void setMaskColor(@ColorInt int maskColor) {
        this.maskColor = maskColor;
    }

    public void setReactColor(@ColorInt int reactColor) {
        this.reactColor = reactColor;
    }

    public void setScanLineColor(@ColorInt int scanLineColor) {
        this.scanLineColor = scanLineColor;
    }

    private void drawMaskView(Canvas canvas, Rect frame, Paint paint) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
        canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);
        canvas.drawRect(0, frame.bottom, width, height, paint);
    }

    private void drawFrameBoundsView(Canvas canvas, Rect frame, Paint boundPaint, Paint reactPaint) {
        canvas.drawRect(frame, boundPaint);

        int corLength = (int) (frame.width() * 0.07);
        int corWidth = (int) (corLength * 0.2);
        corWidth = Math.min(corWidth, 15);

        canvas.drawRect(frame.left - corWidth, frame.top, frame.left, frame.top + corLength, reactPaint);
        canvas.drawRect(frame.left - corWidth, frame.top - corWidth, frame.left + corLength, frame.top, reactPaint);

        canvas.drawRect(frame.right, frame.top, frame.right + corWidth, frame.top + corLength, reactPaint);
        canvas.drawRect(frame.right - corLength, frame.top - corWidth, frame.right + corWidth, frame.top, reactPaint);

        canvas.drawRect(frame.left - corWidth, frame.bottom - corLength, frame.left, frame.bottom, reactPaint);
        canvas.drawRect(frame.left - corWidth, frame.bottom, frame.left + corLength, frame.bottom + corWidth, reactPaint);

        canvas.drawRect(frame.right, frame.bottom - corLength, frame.right + corWidth, frame.bottom, reactPaint);
        canvas.drawRect(frame.right - corLength, frame.bottom, frame.right + corWidth, frame.bottom + corWidth, reactPaint);
    }

    private void drawScanLineView(Canvas canvas, Rect frame, int position, Paint paint) {
        canvas.drawLine(frame.left, position, frame.right, position, paint);
    }

    private Rect getFrame(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        int width = (int) (point.x * 0.6);
        int leftOffset = (point.x - width) / 2;
        int topOffset = (point.y - width) / 5;
        return new Rect(leftOffset, topOffset, leftOffset + width, topOffset + width);
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
