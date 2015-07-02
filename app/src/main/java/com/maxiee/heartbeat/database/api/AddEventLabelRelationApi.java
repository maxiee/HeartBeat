package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;

/**
 * Created by maxiee on 15-6-17.
 */
public class AddEventLabelRelationApi extends BaseDBApi {

    private int mEventKey;
    private int mLabelKey;

    public AddEventLabelRelationApi(Context context,
                                    final int eventKey,
                                    final int labelKey) {
        super(context);
        mEventKey = eventKey;
        mLabelKey = labelKey;
    }

    public boolean exec() {
        ContentValues values = new ContentValues();
        values.put(EventLabelRelationTable.EVENT_ID, mEventKey);
        values.put(EventLabelRelationTable.LABEL_ID, mLabelKey);
        add(EventLabelRelationTable.NAME, values);
        return true;
    }
}
