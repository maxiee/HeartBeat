package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventImageRelationTable;
import com.maxiee.heartbeat.database.tables.ImageTable;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-10.
 */
public class DeleteImageByEventKeyApi extends BaseDBApi{
    private int mEventKey;

    public DeleteImageByEventKeyApi(Context context,
                                    int eventKey) {
        super(context);
        mEventKey = eventKey;
    }

    public void exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventImageRelationTable.NAME,
                new String[] {
                        EventImageRelationTable.IMAGE_ID
                },
                EventImageRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(mEventKey)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return;
        }

        cursor.moveToFirst();
        int[] imageKeys = new int[cursor.getCount()];
        for (int i=0; i<cursor.getCount(); i++) {
            imageKeys[i] = cursor.getInt(
                    cursor.getColumnIndex(EventImageRelationTable.IMAGE_ID)
            );
            cursor.moveToNext();
        }
        cursor.close();
        for (int imageKey : imageKeys) {
            mDatabaseHelper.getWritableDatabase().delete(
                    EventImageRelationTable.NAME,
                    EventImageRelationTable.IMAGE_ID + "=?",
                    new String[]{String.valueOf(imageKey)}
            );
            mDatabaseHelper.getWritableDatabase().delete(
                    ImageTable.NAME,
                    ImageTable.ID + "=?",
                    new String[]{String.valueOf(imageKey)}
            );
        }
    }
}
