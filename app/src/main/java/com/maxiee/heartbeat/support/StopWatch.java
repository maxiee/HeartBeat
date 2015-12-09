package com.maxiee.heartbeat.support;

import android.util.Log;

/**
 * Created by maxiee on 15/12/9.
 */
public class StopWatch {
    private String mWords, mTag;
    private long mCurrent;

    public StopWatch(String tag, String words) {
        mTag = tag;
        mWords = words;
        mCurrent = System.currentTimeMillis();
    }

    public void stop() {
        long now = System.currentTimeMillis();
        Log.d(mTag, String.format("[%.4fs]%s\n", (now - mCurrent) / 1000f, mWords));
    }
}
