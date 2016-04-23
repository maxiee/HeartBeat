package com.maxiee.heartbeat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.agera.database.SqlDatabaseSupplier;

/**
 * Created by maxiee on 16/4/23.
 */
public class HBSqlDatabaseSupplier extends SqlDatabaseSupplier {
    private final static String DB_NAME = "heartbeat";
    private final static int DB_VER = 4;

    public HBSqlDatabaseSupplier(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public static HBSqlDatabaseSupplier databaseSupplier(final Context context) {
        return new HBSqlDatabaseSupplier(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
