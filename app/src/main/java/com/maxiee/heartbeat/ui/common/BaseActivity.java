package com.maxiee.heartbeat.ui.common;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.ThemeUtils;

/**
 * Created by maxiee on 15/10/22.
 */
public class BaseActivity extends AppCompatActivity{
    private int mCurrentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentTheme = ThemeUtils.getCurrentActivityTheme(this);
        setTheme(mCurrentTheme);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ThemeUtils.getAttributeColor(this, R.attr.colorPrimaryDark));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int newTheme = ThemeUtils.getCurrentActivityTheme(this);
        if (mCurrentTheme != newTheme) {
            recreate();
        }
    }
}
