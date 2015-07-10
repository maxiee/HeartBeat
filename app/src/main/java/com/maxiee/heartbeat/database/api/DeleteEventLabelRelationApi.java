package com.maxiee.heartbeat.database.api;

import android.content.Context;

import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;

/**
 * Created by maxiee on 15-7-10.
 */
public class DeleteEventLabelRelationApi extends BaseDBApi{
    private int mEventKey;
    private int mLabelKey;

    public DeleteEventLabelRelationApi(Context context,
                                       int eventKey,
                                       int labelKey) {
        super(context);
        mEventKey = eventKey;
        mLabelKey = labelKey;
    }

    public void exec() {
        mDatabaseHelper.getWritableDatabase().delete(
                EventLabelRelationTable.NAME,
                EventLabelRelationTable.EVENT_ID + "=? and " + EventLabelRelationTable.LABEL_ID + "=?",
                new String[] {String.valueOf(mEventKey), String.valueOf(mLabelKey)}
        );
        new DeleteLabelIfNoUseApi(mContext, mLabelKey).exec();
    }
}
