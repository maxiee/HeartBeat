package com.maxiee.heartbeat.database.api;

import android.content.Context;

import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;
import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;

/**
 * Created by maxiee on 15-7-8.
 */
public class DeleteThoughtByKeyApi extends BaseDBApi{
    private int mThoughtKey;

    public DeleteThoughtByKeyApi(Context context, int thoughtKey) {
        super(context);
        mThoughtKey = thoughtKey;
    }

    public void exec() {
        mDatabaseHelper.getWritableDatabase().delete(
                EventThoughtRelationTable.NAME,
                EventThoughtRelationTable.THOUGHT_ID + "=?",
                new String[] {String.valueOf(mThoughtKey)}
        );
        mDatabaseHelper.getWritableDatabase().delete(
                ThoughtsTable.NAME,
                ThoughtsTable.ID + "=?",
                new String[] {String.valueOf(mThoughtKey)}
        );
        mDatabaseHelper.getWritableDatabase().delete(
                ThoughtResTable.NAME,
                ThoughtResTable.THOUGHT_ID + "=?",
                new String[] {String.valueOf(mThoughtKey)}
        );
    }
}
