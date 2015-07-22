package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-22.
 */
public class EventSearchApi extends BaseDBApi{
    String mSearch;

    public EventSearchApi(Context context, String search) {
        super(context);
        mSearch = search;
    }

    public ArrayList<Event> exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventsTable.NAME,
                new String[] {
                        EventsTable.ID,
                        EventsTable.EVENT,
                        EventsTable.TIMESTAMP
                },
                EventsTable.EVENT + " like ?",
                new String[] {"%" + mSearch + "%"},
                null, null, null
        );

        ArrayList<Event> ret = new ArrayList<>();

        if (cursor.getCount() < 1) {
            return ret;
        }

        cursor.moveToFirst();
        do {
            int id = cursor.getInt(
                    cursor.getColumnIndex(EventsTable.ID)
            );
            String event = cursor.getString(
                    cursor.getColumnIndex(EventsTable.EVENT)
            );

            long timestamp = cursor.getLong(
                    cursor.getColumnIndex(EventsTable.TIMESTAMP)
            );
            ret.add(0, new Event(
                    id,
                    event,
                    timestamp
            ));

        } while (cursor.moveToNext());
        cursor.close();
        return ret;
    }
}
