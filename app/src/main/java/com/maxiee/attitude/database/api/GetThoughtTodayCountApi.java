package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.database.tables.EventsTable;
import com.maxiee.attitude.database.tables.ThoughtsTable;

import java.util.Calendar;

/**
 * Created by maxiee on 15-6-24.
 */
public class GetThoughtTodayCountApi extends BaseDBApi {

    public GetThoughtTodayCountApi(Context context) {
        super(context);
    }

    public int exec() {

        Calendar curDate = Calendar.getInstance();
        curDate.set(
                curDate.get(Calendar.YEAR),
                curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DAY_OF_MONTH),
                0, 0
        );

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                ThoughtsTable.NAME,
                new String[] {
                        ThoughtsTable.ID,
                        ThoughtsTable.TIMESTAMP
                },
                ThoughtsTable.TIMESTAMP + ">?",
                new String[] {String.valueOf(curDate.getTimeInMillis())},
                null, null, null
        );

        int count = cursor.getCount();
        cursor.close();

        if (count < 1) {
            return -1;
        }

        return count;
    }
}
