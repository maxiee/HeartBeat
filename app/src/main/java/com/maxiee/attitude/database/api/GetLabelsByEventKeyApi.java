package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.maxiee.attitude.database.tables.EventLabelRelationTable;
import com.maxiee.attitude.database.tables.LabelsTable;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-17.
 */
public class GetLabelsByEventKeyApi extends BaseDBApi {
    private int mEventKey;

    public GetLabelsByEventKeyApi(Context context,
                                  int eventKey) {
        super(context);
        mEventKey = eventKey;
    }

    public ArrayList<String> exec() {
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

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        ArrayList<Integer> labelIds = new ArrayList<>();
        do {
            int labelId = cursor.getInt(
                    cursor.getColumnIndex(EventLabelRelationTable.LABEL_ID)
            );
            labelIds.add(labelId);
        } while (cursor.moveToNext());

        cursor.close();

        ArrayList<String> labels = new ArrayList<>();
        for (int labelId: labelIds) {
            cursor = mDatabaseHelper.getReadableDatabase().query(
                    LabelsTable.NAME,
                    new String[]{
                            LabelsTable.LABEL
                    },
                    LabelsTable.ID + "=?",
                    new String[]{String.valueOf(labelId)},
                    null, null, null
            );
            if (cursor.getCount()<1) {
                continue;
            }
            cursor.moveToFirst();
            String label = cursor.getString(
                    cursor.getColumnIndex(LabelsTable.LABEL)
            );
            labels.add(label);
        }

        return labels;
    }
}
