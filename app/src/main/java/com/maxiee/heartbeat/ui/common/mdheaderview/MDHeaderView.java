package com.maxiee.heartbeat.ui.common.mdheaderview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.ThemeUtils;

/**
 * Created by maxiee on 15/11/16.
 */
public class MDHeaderView extends View {

    public static final int TRI_NUMS = 4;
    public static final float[] POSITION_X_DELTA = new float[] {0.3f, 0.3f, 0.3f, 0.3f};
    public static final float[] SIZE_Y_DELTA = new float[] {0.3f, 0.2f, 0.4f, 0.6f};

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mColorPrimary;
    private int mColorPrimaryDark;
    private int mColorAccent;
    private TriDrawable[] mDrawables;
    private GradientDrawable mGD;

    public MDHeaderView(Context context) {
        super(context);
        init(context);
    }

    public MDHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MDHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mColorPrimary = ThemeUtils.getAttributeColor(context, R.attr.colorPrimary);
        mColorPrimaryDark = ThemeUtils.getAttributeColor(context, R.attr.colorPrimaryDark);
        mColorAccent = ThemeUtils.getAttributeColor(context, R.attr.colorAccent);
        float redBase = (Color.red(mColorPrimary));
        float greenBase = (Color.green(mColorPrimary));
        float blueBase = (Color.blue(mColorPrimary));
        float redStep = (Color.red(mColorAccent) - redBase) / TRI_NUMS;
        float greenStep = (Color.green(mColorAccent) - greenBase) / TRI_NUMS;
        float blueStep = (Color.blue(mColorAccent) - blueBase) / TRI_NUMS;
        mDrawables = new TriDrawable[TRI_NUMS];
        for (int i = 0; i < TRI_NUMS; i++) {
            mDrawables[i] = new TriDrawable(Color.rgb(
                    (int) (redBase + redStep * (TRI_NUMS - i)),
                    (int) (greenBase + greenStep * (TRI_NUMS - i)),
                    (int) (blueBase + blueStep * (TRI_NUMS - i))));
        }
        mGD = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {mColorPrimaryDark, mColorPrimary});
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xPos = 0;
        float width = w * 0.8f;
        for (int i = 0; i < TRI_NUMS; i++) {
            mDrawables[i].setBounds(
                    (int) xPos,
                    (int) (h * SIZE_Y_DELTA[i]),
                    (int) (xPos + (1 - SIZE_Y_DELTA[i]) * width),
                    h);
            xPos = xPos + POSITION_X_DELTA[i] * width;
        }
        mGD.setBounds(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGD.draw(canvas);
        for (int i = TRI_NUMS - 1; i >=0; i--) {
            mDrawables[i].draw(canvas);
        }
    }
}
