package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;

/**
 * Created by maxiee on 15-6-12.
 */
public class GetOneEventApi extends BaseDBApi {

    private String mId;

    public GetOneEventApi(Context context, int id) {
        super(context);
        mId = String.valueOf(id);
    }

    public Event exec() {

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventsTable.NAME,
                new String[] {
                        EventsTable.ID,
                        EventsTable.EVENT,
                        EventsTable.TIMESTAMP
                },
                EventsTable.ID + "=?",
                new String[] {mId}, null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        int id = cursor.getInt(
                cursor.getColumnIndex(EventsTable.ID)
        );
        String event = cursor.getString(
                cursor.getColumnIndex(EventsTable.EVENT)
        );

        long timestamp = cursor.getLong(
                cursor.getColumnIndex(EventsTable.TIMESTAMP)
        );

        cursor.close();

        return new Event(
                id,
                event,
                timestamp);
    }
}
