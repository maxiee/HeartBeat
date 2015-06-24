package com.maxiee.attitude.database.api;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.maxiee.attitude.common.TimeUtils;
import com.maxiee.attitude.database.tables.EventsTable;
import com.maxiee.attitude.model.Event;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by maxiee on 15-6-24.
 */
public class GetTodayEventApi extends BaseDBApi {

    public GetTodayEventApi(Context context) {
        super(context);
    }

    public ArrayList<Event> exec() {

        Calendar curDate = Calendar.getInstance();
        curDate.set(
                curDate.get(Calendar.YEAR),
                curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DAY_OF_MONTH),
                0, 0
        );

        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventsTable.NAME,
                new String[] {
                        EventsTable.ID,
                        EventsTable.EVENT,
                        EventsTable.TIMESTAMP
                },
                EventsTable.TIMESTAMP + ">?",
                new String[] {String.valueOf(curDate.getTimeInMillis())},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        ArrayList<Event> ret = new ArrayList<>();
        do {
            int id = cursor.getInt(
                    cursor.getColumnIndex(EventsTable.ID)
            );
            String event = cursor.getString(
                    cursor.getColumnIndex(EventsTable.EVENT)
            );
            long timestamp = cursor.getLong(
                    cursor.getColumnIndex(EventsTable.TIMESTAMP)
            );
            ret.add(new Event(
                    id,
                    event,
                    timestamp
            ));
        } while (cursor.moveToNext());

        cursor.close();

        return ret;
    }
}
