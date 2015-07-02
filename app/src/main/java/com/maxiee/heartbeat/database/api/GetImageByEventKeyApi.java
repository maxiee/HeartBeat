package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventImageRelationTable;
import com.maxiee.heartbeat.database.tables.ImageTable;

/**
 * Created by maxiee on 15-7-1.
 */
public class GetImageByEventKeyApi extends BaseDBApi {

    private int mEventKey;

    public GetImageByEventKeyApi(Context context,
                                 int eventKey) {
        super(context);
        mEventKey = eventKey;
    }

    public String exec() {
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
            return null;
        }

        cursor.moveToFirst();
        int imageId = cursor.getInt(
                cursor.getColumnIndex(EventImageRelationTable.IMAGE_ID)
        );
        cursor.close();

        cursor = mDatabaseHelper.getReadableDatabase().query(
                ImageTable.NAME,
                new String[] {
                        ImageTable.URI
                },
                ImageTable.ID + "=?",
                new String[] {String.valueOf(imageId)},
                null, null, null
        );

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();

        String uri = cursor.getString(
                cursor.getColumnIndex(ImageTable.URI)
        );

        cursor.close();

        return uri;
    }
}
