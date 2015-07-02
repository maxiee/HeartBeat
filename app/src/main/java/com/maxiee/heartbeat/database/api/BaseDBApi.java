package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.maxiee.heartbeat.database.DatabaseHelper;

/**
 * Created by maxiee on 15-6-11.
 */
public abstract class BaseDBApi {

    public static final String THOUGHT = "thought";
    public static final String TIMESTAMP = "timestamp";

    protected DatabaseHelper mDatabaseHelper;
    protected Context mContext;

    public BaseDBApi(Context context) {
        mDatabaseHelper = DatabaseHelper.instance(context);
        mContext = context;
    }

    protected long add(String table, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        long key = db.insert(table, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();

        return key;
    };
}
