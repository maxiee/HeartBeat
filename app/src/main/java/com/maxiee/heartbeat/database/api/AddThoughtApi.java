package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.ThoughtsTable;

/**
 * Created by maxiee on 15-6-14.
 */
public class AddThoughtApi extends BaseDBApi{

    private int mEventKey;
    private String mThought;
    private Context mContext;

    public AddThoughtApi(Context context,
                         final int eventKey,
                         final String thought) {
        super(context);
        mContext = context;
        mEventKey = eventKey;
        mThought = thought;
    }

    public boolean exec() {

        ContentValues values = new ContentValues();
        values.put(ThoughtsTable.THOUGHT, mThought);
        values.put(ThoughtsTable.TIMESTAMP, System.currentTimeMillis());

        int thoughtKey = (int) add(ThoughtsTable.NAME, values);

        new AddEventThoughtRelationApi(
                mContext,
                mEventKey,
                thoughtKey).exec();

        return true;
    }
}
