package com.maxiee.heartbeat.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.database.tables.ImageTable;
import com.maxiee.heartbeat.database.utils.ImageUtils;

/**
 * Created by maxiee on 15-9-8.
 */
public class ImageUriUpgradeApi {
    private final static String TAG = ImageUriUpgradeApi.class.getSimpleName();
    private final static String URI_CONTENT_PREFIX = "content://";

    public static void exec(Context context, SQLiteDatabase db) {
        Cursor cursor = db.query(
                ImageTable.NAME,
                new String[] {
                        ImageTable.ID,
                        ImageTable.URI
                },
                null,
                null, null, null, null
        );
        if (cursor.getCount() < 1) {
            Log.d(TAG, "no items in ImageTable");
            return;
        }
        long[] ids = new long[cursor.getCount()];
        String[] uris = new String[cursor.getCount()];
        int count = 0;
        while (cursor.moveToNext()) {
            ids[count] = cursor.getLong(cursor.getColumnIndex(ImageTable.ID));
            uris[count] = cursor.getString(cursor.getColumnIndex(ImageTable.URI));
            count++;
        }
        cursor.close();
        for (int i=0; i<count; i++) {
            updateOrDelete(context, db, ids[i], uris[i]);
        }
    }

    public static void updateOrDelete(Context context, SQLiteDatabase db, long id, String uri) {
        if (uri.startsWith(URI_CONTENT_PREFIX)) {
            String ret = FileUtils.uriToPath(context, Uri.parse(uri));
            if (ret == null) {  // delete
                ImageUtils.deleteByImageId(context, id);
            } else { // update
                ContentValues values = new ContentValues();
                values.put(ImageTable.URI, ret);
                db.update(
                        ImageTable.NAME,
                        values,
                        ImageTable.ID + "=" + String.valueOf(id),
                        null
                );
            }
        }
    }
}
