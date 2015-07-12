package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-10.
 */
public class GetLabelKeysByEventKeyApi extends BaseDBApi {

    private int mEventKey;

    public GetLabelKeysByEventKeyApi(Context context,
                                     int eventKey) {
        super(context);
        mEventKey = eventKey;
    }

    public ArrayList<Integer> exec() {
        // get Labels from relation table
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventLabelRelationTable.NAME,
                new String[] {
                        EventLabelRelationTable.LABEL_ID
                },
                EventLabelRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(mEventKey)},
                null, null, null
        );

        ArrayList<Integer> labelKeys = new ArrayList<>();
        if (cursor.getCount() < 1) {
            return labelKeys;
        }

        cursor.moveToFirst();
        do {
            int labelId = cursor.getInt(
                    cursor.getColumnIndex(EventLabelRelationTable.LABEL_ID)
            );
            labelKeys.add(labelId);
        } while (cursor.moveToNext());

        cursor.close();
        return labelKeys;
    }
}
