package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;
import com.maxiee.heartbeat.model.Thoughts;

import java.util.ArrayList;


/**
 * Created by maxiee on 15-6-21.
 */
public class GetAllThoughtApi extends BaseDBApi {

    private int mEventKey;

    public GetAllThoughtApi(Context context, int mEventKey) {
        super(context);
        this.mEventKey = mEventKey;
    }

    public Thoughts exec() {

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
            return new Thoughts();
        }

        ArrayList<Integer> thoughtIds = new ArrayList<>();

        cursor.moveToFirst();
        do {
            int id = cursor.getInt(
                    cursor.getColumnIndex(EventThoughtRelationTable.THOUGHT_ID)
            );
            thoughtIds.add(id);
        } while (cursor.moveToNext());

        cursor.close();

        Thoughts ret = new Thoughts();

        for (int id: thoughtIds) {
            cursor = mDatabaseHelper.getReadableDatabase().query(
                    ThoughtsTable.NAME,
                    new String[]{
                            ThoughtsTable.ID,
                            ThoughtsTable.THOUGHT,
                            ThoughtsTable.TIMESTAMP
                    },
                    ThoughtsTable.ID + "=?",
                    new String[] {String.valueOf(id)},
                    null, null, null
            );

            if (cursor.getCount() < 1) {
                continue;
            }

            cursor.moveToFirst();

            int key = (int) cursor.getLong(
                    cursor.getColumnIndex(ThoughtsTable.ID)
            );
            String thought = cursor.getString(
                    cursor.getColumnIndex(ThoughtsTable.THOUGHT)
            );
            long time = cursor.getLong(
                    cursor.getColumnIndex(ThoughtsTable.TIMESTAMP)
            );

            ret.add(new Thoughts.Thought(key, thought, time));

        }

        return ret;
    }
}
