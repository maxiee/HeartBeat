package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.database.tables.LabelsTable;

/**
 * Created by maxiee on 15-6-16.
 */
public class HasLabelApi extends BaseDBApi {

    public final static int NOT_FOUND = -1;

    private String mLabel;

    public HasLabelApi(Context context,
                       String label) {
        super(context);
        mLabel = label;
    }

    public int exec() {

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                LabelsTable.NAME,
                new String[] {
                        LabelsTable.ID,
                        LabelsTable.LABEL
                },
                LabelsTable.LABEL + "=?",
                new String[] {mLabel},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return NOT_FOUND;
        }

        cursor.moveToFirst();
        return cursor.getInt(
                cursor.getColumnIndex(LabelsTable.ID)
        );
    }
}
