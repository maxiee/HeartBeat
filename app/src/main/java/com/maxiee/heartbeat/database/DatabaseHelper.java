package com.maxiee.heartbeat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.maxiee.heartbeat.database.api.ImageUriUpgradeApi;
import com.maxiee.heartbeat.database.tables.CrashTable;
import com.maxiee.heartbeat.database.tables.EventImageRelationTable;
import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;
import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;
import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.database.tables.ImageTable;
import com.maxiee.heartbeat.database.tables.LabelsTable;
import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;

/**
 * Created by maxiee on 15-6-11.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "heartbeat";
    private final static int DB_VER = 4;

    private static DatabaseHelper instance;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EventsTable.CREATE);
        db.execSQL(LabelsTable.CREATE);
        db.execSQL(EventLabelRelationTable.CREATE);
        db.execSQL(ThoughtsTable.CREATE);
        db.execSQL(EventThoughtRelationTable.CREATE);
        db.execSQL(ImageTable.CREATE);
        db.execSQL(EventImageRelationTable.CREATE);
        db.execSQL(CrashTable.CREATE);
        db.execSQL(ThoughtResTable.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL(ImageTable.CREATE);
            db.execSQL(EventImageRelationTable.CREATE);
        }
        if (oldVersion <= 2) {
            db.execSQL(CrashTable.CREATE);
        }
        if (oldVersion <= 3) {
            db.execSQL(ThoughtResTable.CREATE);
            // upgrade items in ImageTable
            ImageUriUpgradeApi.exec(mContext, db);
        }
    }

    public static synchronized DatabaseHelper instance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }
}
