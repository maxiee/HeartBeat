package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventImageRelationTable;
import com.maxiee.heartbeat.database.tables.ImageTable;

/**
 * Created by maxiee on 15/11/9.
 */
public class UpdateImageByKeyApi extends BaseDBApi{

    private int mEventKey;
    private String mImagePath;

    public UpdateImageByKeyApi(Context context,
                               final int eventKey,
                               final String path) {
        super(context);
        mEventKey = eventKey;
        mImagePath = path;
    }

    public void exec() {
        Cursor cursor = mDatabaseHelper.getReadableDatabase().query(
                EventImageRelationTable.NAME,
                new String[]{
                        EventImageRelationTable.IMAGE_ID
                },
                EventImageRelationTable.EVENT_ID + "=?",
                new String[]{String.valueOf(mEventKey)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return;
        }

        cursor.moveToFirst();
        int imageId = cursor.getInt(
                cursor.getColumnIndex(EventImageRelationTable.IMAGE_ID)
        );
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(ImageTable.URI, mImagePath);
        mDatabaseHelper.getWritableDatabase().update(
                ImageTable.NAME,
                values,
                ImageTable.ID + "=?",
                new String[] {String.valueOf(imageId)}
        );
    }
}
