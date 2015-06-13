package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.database.tables.EventsTable;
import com.maxiee.attitude.model.Event;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class GetAllEventApi extends BaseDBApi {

    public GetAllEventApi(Context context) {
        super(context);
    }

    public ArrayList<Event> exec() throws JSONException{
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventsTable.NAME,
                new String[] {
                        EventsTable.ID,
                        EventsTable.EVENT,
                        EventsTable.THOUGHTS,
                        EventsTable.LABELS,
                        EventsTable.TIMESTAMP
                },
                null,
                null, null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        ArrayList<Event> eventList = new ArrayList<>();
        do {
            int id = cursor.getInt(
                    cursor.getColumnIndex(EventsTable.ID)
            );
            String event = cursor.getString(
                    cursor.getColumnIndex(EventsTable.EVENT)
            );
            String thoughts = cursor.getString(
                    cursor.getColumnIndex(EventsTable.THOUGHTS)
            );
            JSONArray thoughtsList = new JSONArray(thoughts);
            String labels = cursor.getString(
                    cursor.getColumnIndex(EventsTable.LABELS)
            );
            JSONArray labelsList = new JSONArray(labels);
            int timestamp = cursor.getInt(
                    cursor.getColumnIndex(EventsTable.TIMESTAMP)
            );
            eventList.add(new Event(
                    id,
                    event,
                    thoughtsList,
                    labelsList,
                    timestamp
            ));

        } while (cursor.moveToNext());

        return eventList;
    }
}
