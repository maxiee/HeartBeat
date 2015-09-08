package com.maxiee.heartbeat.database.api;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maxiee.heartbeat.database.tables.EventImageRelationTable;
import com.maxiee.heartbeat.database.tables.ImageTable;

/**
 * Created by maxiee on 15-9-8.
 */
public class DeleteImageByImageKeyApi {

    public static void exec(SQLiteDatabase db, long imageKey) {
        db.delete(
                ImageTable.NAME,
                ImageTable.ID + "=?",
                new String[] {String.valueOf(imageKey)}
        );
        Cursor cursor = db.query(
                EventImageRelationTable.NAME,
                new String[] {
                        EventImageRelationTable.EVENT_ID,
                        EventImageRelationTable.IMAGE_ID
                },
                EventImageRelationTable.IMAGE_ID + "=?",
                new String[] {String.valueOf(imageKey)},
                null, null, null
        );
        if (cursor.getCount() < 1) {
            cursor.close();
            return;
        }
        db.delete(
                EventImageRelationTable.NAME,
                EventImageRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(imageKey)}
        );
        cursor.close();
    }
}
