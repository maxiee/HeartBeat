package com.maxiee.heartbeat.ui.common.mdheaderview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.maxiee.heartbeat.R;

/**
 * Created by maxiee on 15/11/16.
 */
public class TriView extends View{

    public static final int SHADOW_SIZE = 10;

    private Paint mPaint;
    private Path mPath;
    private int mColor;

    public TriView(Context context) {
        super(context);
        init(context, null);
    }

    public TriView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TriView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init (Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TriView, 0, 0);
        mColor = a.getColor(R.styleable.TriView_tri_color, Color.BLACK);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.moveTo(0, getHeight());
        mPath.lineTo(getWidth() / 2, 0);
        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(0, getHeight());
        mPaint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.BLACK);
        mPaint.setColor(mColor);
        canvas.drawPath(mPath, mPaint);
    }

    public void setColor(int color) {
        mColor = color;
    }

}
