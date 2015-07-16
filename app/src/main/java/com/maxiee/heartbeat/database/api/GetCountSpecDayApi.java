package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;

import java.util.Calendar;

/**
 * Created by maxiee on 15-7-16.
 */
public class GetCountSpecDayApi extends BaseDBApi {
    public final static String EVENT = EventsTable.NAME;
    public final static String THOUGHT = ThoughtsTable.NAME;

    public GetCountSpecDayApi(Context context) {
        super(context);
    }

    public int exec(Calendar calendarFrom, String tableName) {
        Calendar calendarTo = (Calendar) calendarFrom.clone();
        calendarTo.add(Calendar.DAY_OF_MONTH, 1);

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                tableName,
                new String[] {
                        EventsTable.ID
                },
                EventsTable.TIMESTAMP + ">? and " + EventsTable.TIMESTAMP + "<?",
                new String[] {
                        String.valueOf(calendarFrom.getTimeInMillis()),
                        String.valueOf(calendarTo.getTimeInMillis())
                },
                null, null, null
        );

        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
