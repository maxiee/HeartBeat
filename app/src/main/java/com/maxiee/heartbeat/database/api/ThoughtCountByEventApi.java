package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;

/**
 * Created by maxiee on 15-6-21.
 */
public class ThoughtCountByEventApi extends BaseDBApi{

    private int mEventKey;

    public ThoughtCountByEventApi(Context context,
                                  int eventKey) {
        super(context);
        mEventKey = eventKey;
    }

    public int exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventThoughtRelationTable.NAME,
                new String[] {
                        EventThoughtRelationTable.THOUGHT_ID
                },
                EventThoughtRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(mEventKey)},
                null, null, null
        );

        int count = cursor.getCount();
        cursor.close();

        return count;
    }
}
