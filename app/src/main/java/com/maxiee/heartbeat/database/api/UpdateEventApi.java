package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.EventsTable;

/**
 * Created by maxiee on 15-7-10.
 */
public class UpdateEventApi extends BaseDBApi{
    private int mKey;
    private String mEvent;

    public UpdateEventApi(Context context,
                          int key,
                          String event) {
        super(context);
        mKey = key;
        mEvent = event;
    }

    public void exec() {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, mEvent);
        mDatabaseHelper.getWritableDatabase().update(
                EventsTable.NAME,
                values,
                EventsTable.ID + "=?",
                new String[] {String.valueOf(mKey)}
        );
    }
}
