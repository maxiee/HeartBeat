package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;

import com.maxiee.attitude.database.tables.EventsTable;
import com.maxiee.attitude.model.Thoughts;

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

    private String convertJSONString(final ArrayList<String> list) {
        JSONArray array = new JSONArray(list);
        return array.toString();
    }

    private String convertThought(final String thought) throws JSONException{
        Thoughts thoughtObject = new Thoughts();
        thoughtObject.addThought(
                thought
        );
        return thoughtObject.getmThoughts().toString();
    }

    public long exec() {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, mEvent);
        values.put(EventsTable.TIMESTAMP, System.currentTimeMillis());
        return add(EventsTable.NAME, values);
    }
}
