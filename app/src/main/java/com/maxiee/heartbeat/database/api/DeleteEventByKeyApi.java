package com.maxiee.heartbeat.database.api;

import android.content.Context;

import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;
import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-10.
 */
public class DeleteEventByKeyApi extends BaseDBApi{
    private int mEventkey;

    public DeleteEventByKeyApi(Context context,
                               int eventKey) {
        super(context);
        mEventkey = eventKey;
    }

    public void exec() {
        mDatabaseHelper.getWritableDatabase().delete(
                EventsTable.NAME,
                EventsTable.ID + "=?",
                new String[] {String.valueOf(mEventkey)}
        );
        ArrayList<Integer> labelKeys = new GetLabelKeysByEventKeyApi(mContext, mEventkey).exec();
        mDatabaseHelper.getWritableDatabase().delete(
                EventLabelRelationTable.NAME,
                EventLabelRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(mEventkey)}
        );
        for (int key: labelKeys) {
            new DeleteLabelIfNoUseApi(mContext, key).exec();
        }
    }
}
