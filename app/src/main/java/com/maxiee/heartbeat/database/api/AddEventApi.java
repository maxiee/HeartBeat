package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Thoughts;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class AddEventApi extends BaseDBApi {

    private String mEvent;

    public AddEventApi(Context context,
                       final String event) {
        super(context);
        mEvent = event;
    }

    public long exec() {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, mEvent);
        values.put(EventsTable.TIMESTAMP, System.currentTimeMillis());
        return add(EventsTable.NAME, values);
    }
}
