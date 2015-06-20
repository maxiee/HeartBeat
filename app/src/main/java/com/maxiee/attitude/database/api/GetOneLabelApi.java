package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.database.tables.LabelsTable;

/**
 * Created by maxiee on 15-6-18.
 */
public class GetOneLabelApi extends BaseDBApi {

    private int mLabelId;

    public GetOneLabelApi(Context context,
                          final int labelId) {
        super(context);
        mLabelId = labelId;
    }

    public String exec() {

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                LabelsTable.NAME,
                new String[] {
                        LabelsTable.LABEL
                },
                LabelsTable.ID + "=?",
                new String[] {String.valueOf(mLabelId)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        return cursor.getString(
                cursor.getColumnIndex(LabelsTable.LABEL)
        );
    }
}
