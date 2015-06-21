package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.attitude.database.tables.EventThoughtRelationTable;

/**
 * Created by maxiee on 15-6-21.
 */
public class AddEventThoughtRelationApi extends BaseDBApi {

    private int mEventKey;
    private int mThoughtKey;


    public AddEventThoughtRelationApi(Context context,
                                      final int eventKey,
                                      final int thoughtKey) {
        super(context);
        mEventKey = eventKey;
        mThoughtKey = thoughtKey;
    }

    public boolean exec() {
        ContentValues values = new ContentValues();
        values.put(EventThoughtRelationTable.EVENT_ID, mEventKey);
        values.put(EventThoughtRelationTable.THOUGHT_ID, mThoughtKey);
        add(EventThoughtRelationTable.NAME, values);

        return true;
    }
}
