package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;
import com.maxiee.heartbeat.database.tables.LabelsTable;
import com.maxiee.heartbeat.model.Label;

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
        ArrayList<Integer> labelKeys = new GetLabelKeysByEventKeyApi(mContext, mEventKey).exec();
        if (labelKeys==null) return null;
        ArrayList<String> labels = new ArrayList<>();
        for (int labelId: labelKeys) {
            Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
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
