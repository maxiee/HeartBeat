package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.ThoughtResTable;

/**
 * Created by maxiee on 15/9/17.
 */
public class UpdateThoughtResByKey extends BaseDBApi{

    private int mThoughtKey;
    private int mResType;
    private String mResPath;


    public UpdateThoughtResByKey(Context context,
                                 int thoughtKey,
                                 int resType,
                                 String resPath) {
        super(context);
        mThoughtKey = thoughtKey;
        mResType = resType;
        mResPath = resPath;
    }

    public void exec() {
        ContentValues values = new ContentValues();
        values.put(ThoughtResTable.TYPE, mResType);
        values.put(ThoughtResTable.PATH, mResPath);
        mDatabaseHelper.getWritableDatabase().update(
                ThoughtResTable.NAME,
                values,
                ThoughtResTable.THOUGHT_ID + "=" + String.valueOf(mThoughtKey),
                null

        );
    }
}
