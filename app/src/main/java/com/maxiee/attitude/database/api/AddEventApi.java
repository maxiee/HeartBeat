package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;

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
    private String mThought;

    public AddEventApi(Context context,
                       final String event,
                       final String thought) {
        super(context);
        mEvent = event;
        mThought = thought;
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

    public boolean exec() throws JSONException{
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, mEvent);
        values.put(EventsTable.THOUGHTS, convertThought(mThought));
        values.put(EventsTable.TIMESTAMP, System.currentTimeMillis());
        add(EventsTable.NAME, values);
        return true;
    }
}
