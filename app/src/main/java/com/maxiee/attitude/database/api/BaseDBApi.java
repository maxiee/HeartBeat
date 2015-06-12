package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.maxiee.attitude.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-11.
 */
public abstract class BaseDBApi {
    protected DatabaseHelper mDatabaseHelper;

    public BaseDBApi(Context context) {
        mDatabaseHelper = DatabaseHelper.instance(context);
    }

    protected void add(String table, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        db.insert(table, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    };
}
