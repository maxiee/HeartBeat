package com.maxiee.heartbeat.support;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.maxiee.heartbeat.database.api.AddCrashApi;

import org.apache.http.util.ExceptionUtils;

/**
 * Created by maxiee on 15-7-11.
 *
 * Learning from BlackLight (https://github.com/PaperAirplane-Dev-Team/BlackLight)
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler{

    private static String ANDROID = Build.VERSION.RELEASE;
    private static String MODEL = Build.MODEL;
    private static String MANUFACTURER = Build.MANUFACTURER;

    public static String VERSION = "Unknown";

    private Thread.UncaughtExceptionHandler mPrevious;
    private Context mContext;

    public void init(Context context) {
        mContext = context;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION = info.versionName + " (" + info.versionCode + ")";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(Context context) {
        new CrashHandler().init(context);
    }

    private CrashHandler() {
        mPrevious = Thread.currentThread().getUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String log = "";

        log += "Android Version: " + ANDROID + "\n";
        log += "Device Model: " + MODEL + "\n";
        log += "Device Manufacturer: " + MANUFACTURER + "\n";
        log += "App Version: " + VERSION + "\n";
        log += "*********************\n";
        log += throwable.toString() + "\n";
        log += Log.getStackTraceString(throwable);

        new AddCrashApi(mContext, log).exec();

        if (mPrevious != null) {
            mPrevious.uncaughtException(thread, throwable);
        }
    }
}
