package com.maxiee.heartbeat.ui.common.mdheaderview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.ThemeUtils;

/**
 * Created by maxiee on 15/11/16.
 */
public class MDHeaderView extends ViewGroup {

    public static final int TRI_NUMS = 6;

    private int mColorPrimary;
    private int mColorPrimaryDark;
    private int mColorAccent;
    private float[] mSizes;
    private TriView[] mViews;
    private AnimatorSet[] mSets;
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
        mViews = new TriView[TRI_NUMS];
        mSizes = new float[TRI_NUMS];
        mSets = new AnimatorSet[TRI_NUMS];
        for (int i = TRI_NUMS - 1; i >= 0; i--) {
            mSizes[i] = (float) Math.abs(Math.sin(Math.PI / 4 + Math.PI / (TRI_NUMS * 1.5f) * i));
            mSets[i] = new AnimatorSet();
            mViews[i] = new TriView(context);
            mViews[i].setColor(Color.rgb(
                    (int) (redBase + redStep * (TRI_NUMS - i)),
                    (int) (greenBase + greenStep * (TRI_NUMS - i)),
                    (int) (blueBase + blueStep * (TRI_NUMS - i))));
            addView(mViews[i]);
        }
        mGD = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {mColorPrimaryDark, mColorPrimary});
        setBackgroundDrawable(mGD);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int height = getHeight();
        int weight = width / (TRI_NUMS + 2);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(childCount - 1 - i);
            childView.layout(
                    weight * i,
                    (int) (b + 1),
                    (int) (i * weight + mSizes[i] * height),
                    (int) (b + mSizes[i] * height + 1));
            mSets[i].playTogether(
                    ObjectAnimator.ofFloat(mViews[i], "translationY", -mSizes[i] * height),
                    ObjectAnimator.ofFloat(mViews[i], "alpha", 0f, 1f)
            );
            mSets[i].setStartDelay(150 + 50 * i);
            mSets[i].setDuration(600).start();
        }
    }
}
