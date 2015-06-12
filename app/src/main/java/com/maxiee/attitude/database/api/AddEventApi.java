package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.maxiee.attitude.database.tables.EventsTable;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class AddEventApi extends BaseDBApi {

    private String mEvent;
    private ArrayList<String> mThought;
    private ArrayList<String> mLabels;

    public AddEventApi(Context context,
                       final String event,
                       final String thought,
                       final ArrayList<String> labels) {
        super(context);
        mEvent = event;
        mThought = new ArrayList<>();
        mThought.add(thought);
        mLabels = labels;
    }

    private String convertJSONString(final ArrayList<String> list) {
        JSONArray array = new JSONArray(list);
        return array.toString();
    }

    public boolean exec() {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, mEvent);
        values.put(EventsTable.THOUGHTS, convertJSONString(mThought));
        values.put(EventsTable.LABELS, convertJSONString(mLabels));
        values.put(EventsTable.TIMESTAMP, System.currentTimeMillis());
        add(EventsTable.NAME, values);
        return true;
    }
}
