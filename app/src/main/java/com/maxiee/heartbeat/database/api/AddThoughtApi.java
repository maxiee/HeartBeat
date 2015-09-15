package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;
import com.maxiee.heartbeat.model.Thoughts;

/**
 * Created by maxiee on 15-6-14.
 */
public class AddThoughtApi extends BaseDBApi{

    private int mEventKey;
    private String mThought;
    private int mResType;
    private String mResPath;
    private Context mContext;

    public AddThoughtApi(Context context,
                         final int eventKey,
                         final String thought,
                         final int resType,
                         final String resPath) {
        super(context);
        mContext = context;
        mEventKey = eventKey;
        mThought = thought;
        mResType = resType;
        mResPath = resPath;
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

        if (mResType == Thoughts.Thought.HAS_NO_RES) return true;

        ContentValues resValues = new ContentValues();
        resValues.put(ThoughtResTable.THOUGHT_ID, thoughtKey);
        resValues.put(ThoughtResTable.TYPE, mResType);
        resValues.put(ThoughtResTable.PATH, mResPath);

        int resKey = (int) add(ThoughtResTable.NAME, resValues);

        return true;
    }
}
