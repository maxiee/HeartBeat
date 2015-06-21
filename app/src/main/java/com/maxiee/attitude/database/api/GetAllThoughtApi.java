package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.common.TimeUtils;
import com.maxiee.attitude.database.tables.EventThoughtRelationTable;
import com.maxiee.attitude.database.tables.ThoughtsTable;
import com.maxiee.attitude.model.Thoughts;

import java.util.ArrayList;

import static com.maxiee.attitude.model.Thoughts.*;


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
            return null;
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

            String thought = cursor.getString(
                    cursor.getColumnIndex(ThoughtsTable.THOUGHT)
            );
            long time = cursor.getLong(
                    cursor.getColumnIndex(ThoughtsTable.TIMESTAMP)
            );

            ret.add(new Thoughts.Thought(thought, time));

        }

        return ret;
    }
}
