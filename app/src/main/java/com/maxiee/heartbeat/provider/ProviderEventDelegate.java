package com.maxiee.heartbeat.provider;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.maxiee.heartbeat.database.api.GetCountByNameApi;
import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.database.utils.DatabaseUtils;

import java.util.Random;

/**
 * Created by maxiee on 16/1/10.
 */
public class ProviderEventDelegate implements IProviderEventDelegate {
    @Override
    public Cursor dispatchQuery(int code, Context context, Uri uri) {
        switch (code) {
            case Constant.API_EVENT_RANDOM_CODE: {
                return random(context);
            }
            case Constant.API_EVENT_ID_CODE: {
                long id =  ContentUris.parseId(uri);
                return byId(context, id);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public Cursor random(Context context) {
        long eventCount = new GetCountByNameApi(context).exec(GetCountByNameApi.EVENT);
        int randIndex = (int) (new Random().nextDouble() * eventCount);
        Cursor cursor = DatabaseUtils.getReadableDatabase(context).rawQuery(
                "SELECT " + EventsTable.ID + ", " + EventsTable.EVENT + ", " + EventsTable.TIMESTAMP
                + " FROM " + EventsTable.NAME + " LIMIT 1 OFFSET " + String.valueOf(randIndex), null);
        return cursor;
    }

    @Override
    public Cursor byId(Context context, long id) {
        Cursor cursor = DatabaseUtils.getReadableDatabase(context).rawQuery(
                "SELECT " + EventsTable.ID + ", " + EventsTable.EVENT + ", " + EventsTable.TIMESTAMP
                        + " FROM " + EventsTable.NAME + " LIMIT 1 OFFSET " + String.valueOf(id), null);
        return cursor;
    }
}
