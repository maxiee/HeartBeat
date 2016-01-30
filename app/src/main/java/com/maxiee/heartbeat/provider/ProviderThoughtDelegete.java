package com.maxiee.heartbeat.provider;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;
import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;
import com.maxiee.heartbeat.database.utils.DatabaseUtils;

/**
 * Created by maxiee on 16/1/31.
 */
public class ProviderThoughtDelegete implements IProviderThoughtDelegate{
    @Override
    public Cursor dispatchQuery(int code, Context context, Uri uri) {
        switch (code) {
            case Constant.API_THOUGHT_EVENT_ID_CODE: {
                long id =  ContentUris.parseId(uri);
                return getThought(context, id);
            }
            case Constant.API_THOUGHT_RES_THOUGHT_ID_CODE: {
                long id =  ContentUris.parseId(uri);
                return getThoughtRes(context, id);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public Cursor getThought(Context context, long eventId) {
        final String rawSQL =
                "SELECT " +  ThoughtsTable.NAME + "." + ThoughtsTable.ID + ", " + ThoughtsTable.THOUGHT + ", " + ThoughtsTable.TIMESTAMP + ", "
                        + EventThoughtRelationTable.EVENT_ID + " FROM " + ThoughtsTable.NAME + " JOIN " + EventThoughtRelationTable.NAME
                        + " ON " + ThoughtsTable.NAME + "." + ThoughtsTable.ID + "=" + EventThoughtRelationTable.NAME + "." + EventThoughtRelationTable.THOUGHT_ID
                        + " WHERE " + EventThoughtRelationTable.EVENT_ID + "=?"
                        + " ORDER BY " + ThoughtsTable.TIMESTAMP + " ASC;";
        return DatabaseUtils.getReadableDatabase(context).rawQuery(
                rawSQL,
                new String[] { String.valueOf(eventId)});
    }

    @Override
    public Cursor getThoughtRes(Context context, long thoughtId) {
        return DatabaseUtils.query(
                context, ThoughtResTable.NAME,
                new String[] {ThoughtResTable.ID, ThoughtResTable.THOUGHT_ID, ThoughtResTable.TYPE, ThoughtResTable.PATH},
                ThoughtResTable.THOUGHT_ID + "=?",
                new String[] {String.valueOf(thoughtId)});
    }
}
