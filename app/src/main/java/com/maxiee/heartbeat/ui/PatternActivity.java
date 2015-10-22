package com.maxiee.heartbeat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.eftimoff.patternview.PatternView;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.ui.common.BaseActivity;

/**
 * Created by maxiee on 15-7-29.
 */
public class PatternActivity extends BaseActivity{
    public final static String ACTION = "action";
    public final static int SET = 0;
    public final static int VERIFY = 1;
    public final static int MODIFY = 2; // verify then set
    public final static int CANCEL = 3; // verify then clear
    public final static int ERROR = -1;

    private final static int SET_1_STAGE = 10;
    private final static int SET_2_STAGE = 11;
    private final static int CANCEL_1_STAGE = 20;
    private final static int MODIFY_1_STAGE = 30;
    private final static int MODIFY_2_STAGE = 31;
    private final static int MODIFY_3_STAGE = 32;
    private final static int VERIFY_1_STAGE = 40;

    private SharedPreferences mPrefs;
    private PatternView mPatternView;
    private TextView mTvPatternHint;
    private int mCurrentStatus;
    private String mPattern;
    private String mPatternBak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);

        mTvPatternHint = (TextView) findViewById(R.id.pattern_hint);
        mPatternView = (PatternView) findViewById(R.id.pattern);
        mPatternView.setOnPatternDetectedListener(new PatternDetected());
        mPrefs = getSharedPreferences("hb", Context.MODE_PRIVATE);

        Intent i = getIntent();
        int action = i.getIntExtra(ACTION, ERROR);
        switch (action) {
            case SET:
                setStageOne();
                break;
            case CANCEL:
                cancelStageOne();
                break;
            case MODIFY:
                modifyStageOne();
                break;
            case VERIFY:
                mTvPatternHint.setText(getString(R.string.input_pattern));
                mCurrentStatus = VERIFY_1_STAGE;
                break;
        }
    }

    private void setStageOne() {
        mTvPatternHint.setText(getString(R.string.input_pattern));
        mCurrentStatus = SET_1_STAGE;
    }

    private void setStageTwo() {
        mTvPatternHint.setText(getString(R.string.verify_pattern));
        mCurrentStatus = SET_2_STAGE;
        mPatternBak = mPattern;
    }

    private void cancelStageOne() {
        mTvPatternHint.setText(getString(R.string.input_pattern));
        mCurrentStatus = CANCEL_1_STAGE;
    }

    private void cancelFinished() {
        if (verifyPattern(mPattern, getSPPattern())) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("pattern", "");
            editor.apply();
            finish();
        } else {
            mTvPatternHint.setText(R.string.input_pattern_error);
        }
    }

    private void modifyStageOne() {
        mTvPatternHint.setText(getString(R.string.input_pattern));
        mCurrentStatus = MODIFY_1_STAGE;
    }

    private void modifyStageTwo() {
        if (mCurrentStatus == MODIFY_1_STAGE) {
            if (verifyPattern(mPattern, getSPPattern())) {
                mTvPatternHint.setText(getString(R.string.input_new_pattern));
                mCurrentStatus = MODIFY_2_STAGE;
            } else {
                mTvPatternHint.setText(R.string.input_pattern_error);
            }
        } else if (mCurrentStatus == MODIFY_3_STAGE) {
            mTvPatternHint.setText(getString(R.string.input_new_pattern));
            mCurrentStatus = MODIFY_2_STAGE;
        }
    }

    private void modifyStageThree() {
        mTvPatternHint.setText(getString(R.string.verify_pattern));
        mCurrentStatus = MODIFY_3_STAGE;
        mPatternBak = mPattern;
    }

    private void setFinished() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("pattern", mPattern);
        editor.apply();
        finish();
    }

    private boolean verifyPattern(String pattern1, String pattern2) {
        return  (pattern1.equals(pattern2));
    }

    private String getSPPattern() {
        return mPrefs.getString("pattern", "");
    }

    private class PatternDetected implements PatternView.OnPatternDetectedListener {
        @Override
        public void onPatternDetected() {
            mPattern = mPatternView.getPatternString();
            mPatternView.clearPattern();
            if (mCurrentStatus == SET_1_STAGE) {
                setStageTwo();
            } else if (mCurrentStatus == SET_2_STAGE) {
                if (verifyPattern(mPattern, mPatternBak)) {
                    setFinished();
                } else {
                    setStageOne();
                }
            } else if (mCurrentStatus == CANCEL_1_STAGE) {
                cancelFinished();
            } else if (mCurrentStatus == MODIFY_1_STAGE) {
                modifyStageTwo();   // 密码验证成功，准备设置新密码
            } else if (mCurrentStatus == MODIFY_2_STAGE) {
                modifyStageThree(); // 输入新密码
            } else if (mCurrentStatus == MODIFY_3_STAGE) {
                if (verifyPattern(mPattern, mPatternBak)) {
                    setFinished();
                } else {
                    modifyStageTwo();
                }
            } else if (mCurrentStatus == VERIFY_1_STAGE) {
                if (verifyPattern(mPattern, getSPPattern())) {
                    Intent i = new Intent(PatternActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    mTvPatternHint.setText(R.string.input_pattern_error);
                }
            }
        }
    }
}
