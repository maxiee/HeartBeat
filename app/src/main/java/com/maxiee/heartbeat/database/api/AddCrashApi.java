package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.CrashTable;

/**
 * Created by maxiee on 15-7-11.
 */
public class AddCrashApi extends BaseDBApi{

    private String mLog;

    public AddCrashApi(Context context,
                       String log) {
        super(context);
        mLog = log;
    }

    public void exec() {
        ContentValues values = new ContentValues();
        values.put(CrashTable.LOG, mLog);
        values.put(CrashTable.TIMESTAMP, System.currentTimeMillis());
        add(CrashTable.NAME, values);
    }
}
