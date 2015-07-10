package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class GetAllEventApi extends BaseDBApi {

    public GetAllEventApi(Context context) {
        super(context);
    }

    public ArrayList<Event> exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventsTable.NAME,
                new String[] {
                        EventsTable.ID,
                        EventsTable.EVENT,
                        EventsTable.TIMESTAMP
                },
                null,
                null, null, null, null
        );

        ArrayList<Event> eventList = new ArrayList<>();

        if (cursor.getCount() < 1) {
            return eventList;
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
            eventList.add(0, new Event(
                    id,
                    event,
                    timestamp
            ));

        } while (cursor.moveToNext());

        cursor.close();

        return eventList;
    }
}
