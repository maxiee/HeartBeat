package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;

/**
 * Created by maxiee on 15-7-10.
 */
public class DeleteThoughtsByEventKeyApi extends BaseDBApi{
    private int mEventKey;

    public DeleteThoughtsByEventKeyApi(Context context,
                                       int eventKey) {
        super(context);
        mEventKey = eventKey;
    }

    public void exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventThoughtRelationTable.NAME,
                new String[] {
                        EventThoughtRelationTable.THOUGHT_ID
                },
                EventThoughtRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(mEventKey)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return;
        }

        cursor.moveToFirst();
        int[] thoughtKeys = new int[cursor.getCount()];
        for (int i=0; i<cursor.getCount(); i++) {
            thoughtKeys[i] = cursor.getInt(
                    cursor.getColumnIndex(EventThoughtRelationTable.THOUGHT_ID)
            );
            cursor.moveToNext();
        }
        cursor.close();
        for (int thoughtKey : thoughtKeys) {
            new DeleteThoughtByKeyApi(mContext, thoughtKey).exec();
        }
    }
}
