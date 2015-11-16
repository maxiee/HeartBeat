package com.maxiee.heartbeat.ui.common.mdheaderview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by maxiee on 15/11/16.
 */
public class TriDrawable extends Drawable{

    public static final int SHADOW_SIZE = 10;

    private Paint mPaint;
    private Path mPath;
    private int mColor;

    public TriDrawable(int color) {
        mColor = color;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        int top = bounds.top;
        int bottom = bounds.bottom;
        int left = bounds.left;
        int right = bounds.right;
        int centerX = bounds.centerX();
        mPath.moveTo(left, bottom);
        mPath.lineTo(centerX, top);
        mPath.lineTo(right, bottom);
        mPath.lineTo(left, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.BLACK);
        mPaint.setColor(mColor);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
