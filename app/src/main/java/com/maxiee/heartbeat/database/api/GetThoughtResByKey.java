package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.model.Thoughts;

/**
 * Created by maxiee on 15-9-14.
 */
public class GetThoughtResByKey extends BaseDBApi{

    private int mThoughtKey;
    private int mResType;
    private String mResPath;

    public GetThoughtResByKey(Context context, int thoughtKey) {
        super(context);
        mThoughtKey = thoughtKey;
    }

    public void exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                ThoughtResTable.NAME,
                new String[] {
                        ThoughtResTable.THOUGHT_ID,
                        ThoughtResTable.TYPE,
                        ThoughtResTable.PATH
                },
                ThoughtResTable.THOUGHT_ID + "=?",
                new String[] {String.valueOf(mThoughtKey)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            mResType = Thoughts.Thought.HAS_NO_RES;
            cursor.close();
            return;
        }

        cursor.moveToFirst();

        mResType = cursor.getInt(cursor.getColumnIndex(ThoughtResTable.TYPE));
        mResPath = cursor.getString(cursor.getColumnIndex(ThoughtResTable.PATH));
        cursor.close();
    }

    public int getType() {return mResType;}

    public String getPath() {return mResPath;}
}
