package com.maxiee.heartbeat;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by maxiee on 16/4/18.
 */
public class HeartBeatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
