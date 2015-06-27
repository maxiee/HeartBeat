package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.database.tables.EventLabelRelationTable;
import com.maxiee.attitude.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-27.
 */
public class GetEventsByLabelKeyApi extends BaseDBApi{
    private int mLabelKey;

    public GetEventsByLabelKeyApi(Context context,
                                  int labelKey) {
        super(context);
        mLabelKey = labelKey;
    }

    public ArrayList<Event> exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventLabelRelationTable.NAME,
                new String[] {
                        EventLabelRelationTable.EVENT_ID
                },
                EventLabelRelationTable.LABEL_ID + "=?",
                new String[] {String.valueOf(mLabelKey)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        ArrayList<Event> ret = new ArrayList<>();
        do {
            int eventId = cursor.getInt(
                    cursor.getColumnIndex(EventLabelRelationTable.EVENT_ID)
            );
            Event event = new GetOneEventApi(mContext, eventId).exec();
            ret.add(event);
        } while (cursor.moveToNext());

        return ret;
    }
}
