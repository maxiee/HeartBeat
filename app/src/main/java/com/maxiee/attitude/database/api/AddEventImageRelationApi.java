package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.attitude.database.tables.EventImageRelationTable;

/**
 * Created by maxiee on 15-7-1.
 */
public class AddEventImageRelationApi extends BaseDBApi{

    private int mEventKey;
    private int mImageKey;

    public AddEventImageRelationApi(Context context,
                                    final int eventkey,
                                    final int imageKey) {
        super(context);
        mEventKey = eventkey;
        mImageKey = imageKey;
    }

    public boolean exec() {
        ContentValues values = new ContentValues();
        values.put(EventImageRelationTable.EVENT_ID, mEventKey);
        values.put(EventImageRelationTable.IMAGE_ID, mImageKey);
        add(EventImageRelationTable.NAME, values);

        return true;
    }
}
