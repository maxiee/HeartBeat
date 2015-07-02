package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.LabelsTable;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-18.
 */
public class GetAllLabelApi extends BaseDBApi {

    public GetAllLabelApi(Context context) {
        super(context);
    }

    public ArrayList<Integer> exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                LabelsTable.NAME,
                new String[] {
                        LabelsTable.ID
                },
                null,
                null, null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        ArrayList<Integer> ret = new ArrayList<>();

        cursor.moveToFirst();
        do {
            int id = cursor.getInt(
                    cursor.getColumnIndex(LabelsTable.ID)
            );
            ret.add(id);
        } while (cursor.moveToNext());

        cursor.close();

        return ret;

    }
}
