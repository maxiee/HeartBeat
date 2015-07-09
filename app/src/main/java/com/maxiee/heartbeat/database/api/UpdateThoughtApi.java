package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.ThoughtsTable;

/**
 * Created by maxiee on 15-7-8.
 */
public class UpdateThoughtApi extends BaseDBApi{
    private int mKey;
    private String mThought;

    public UpdateThoughtApi(Context context,
                            int key,
                            String thought) {
        super(context);
        mKey = key;
        mThought = thought;
    }

    public void exec() {
        ContentValues values = new ContentValues();
        values.put(ThoughtsTable.THOUGHT, mThought);
        mDatabaseHelper.getWritableDatabase().update(
                ThoughtsTable.NAME,
                values,
                ThoughtsTable.ID + "=" + String.valueOf(mKey),
                null
        );
    }
}
