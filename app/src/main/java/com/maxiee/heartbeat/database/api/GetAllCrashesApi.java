package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.CrashTable;
import com.maxiee.heartbeat.model.Crash;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-11.
 */
public class GetAllCrashesApi extends BaseDBApi {
    public GetAllCrashesApi(Context context) {
        super(context);
    }

    public ArrayList<Crash> exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                CrashTable.NAME,
                new String[] {
                        CrashTable.LOG,
                        CrashTable.TIMESTAMP
                },
                null,
                null, null, null, null
        );

        ArrayList<Crash> crashes = new ArrayList<>();

        if (cursor.getCount() < 1)
            return crashes;

        cursor.moveToFirst();
        do {
            String log = cursor.getString(
                    cursor.getColumnIndex(CrashTable.LOG)
            );
            long timestamp = cursor.getLong(
                    cursor.getColumnIndex(CrashTable.TIMESTAMP)
            );
            crashes.add(0, new Crash(log, timestamp));
        } while (cursor.moveToNext());

        cursor.close();
        return crashes;
    }
}
