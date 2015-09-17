package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.ThoughtResTable;

/**
 * Created by maxiee on 15/9/17.
 */
public class AddThoughtResByKeyApi extends BaseDBApi{

    private int mThoughtKey;
    private int mResType;
    private String mResPath;

    public AddThoughtResByKeyApi(Context context,
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
        values.put(ThoughtResTable.THOUGHT_ID, mThoughtKey);
        values.put(ThoughtResTable.TYPE, mResType);
        values.put(ThoughtResTable.PATH, mResPath);
        add(ThoughtResTable.NAME, values);
    }
}
