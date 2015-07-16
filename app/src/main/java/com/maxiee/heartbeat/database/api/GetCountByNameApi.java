package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;

/**
 * Created by maxiee on 15-7-16.
 */
public class GetCountByNameApi extends BaseDBApi{
    public final static String EVENT = EventsTable.NAME;
    public final static String THOUGHT = ThoughtsTable.NAME;

    public GetCountByNameApi(Context context) {
        super(context);
    }

    public long exec(String table) {
        return DatabaseUtils.queryNumEntries(mDatabaseHelper.getReadableDatabase(), table);
    }
}
