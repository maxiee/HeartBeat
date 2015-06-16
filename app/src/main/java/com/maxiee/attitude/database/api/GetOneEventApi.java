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
public class GetOneEventApi extends BaseDBApi {

    private String mId;

    public GetOneEventApi(Context context, int id) {
        super(context);
        mId = String.valueOf(id);
    }

    public Event exec() throws JSONException{

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventsTable.NAME,
                new String[] {
                        EventsTable.ID,
                        EventsTable.EVENT,
                        EventsTable.THOUGHTS,
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
        String thoughts = cursor.getString(
                cursor.getColumnIndex(EventsTable.THOUGHTS)
        );
        JSONArray thoughtsList = new JSONArray(thoughts);
        int timestamp = cursor.getInt(
                cursor.getColumnIndex(EventsTable.TIMESTAMP)
        );

        cursor.close();

        return new Event(
                id,
                event,
                thoughtsList,
                timestamp);
    }
}
