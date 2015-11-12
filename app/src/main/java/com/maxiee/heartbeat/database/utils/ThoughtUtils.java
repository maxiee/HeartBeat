package com.maxiee.heartbeat.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.tables.EventThoughtRelationTable;
import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.database.tables.ThoughtsTable;
import com.maxiee.heartbeat.model.ThoughtRes;
import com.maxiee.heartbeat.model.Thoughts;

/**
 * Created by maxiee on 15/11/11.
 */
public class ThoughtUtils {

    private static long queryId(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, EventThoughtRelationTable.THOUGHT_ID);
    }

    private static long queryThoughtId(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, ThoughtsTable.ID);
    }

    private static String queryThoughtStr(Cursor cursor) {
        return DatabaseUtils.getString(cursor, ThoughtsTable.THOUGHT);
    }

    private static long queryTiemStamp(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, ThoughtsTable.TIMESTAMP);
    }

    private static Thoughts.Thought queryThought(Cursor cursor) {
        return new Thoughts.Thought(queryThoughtId(cursor), queryThoughtStr(cursor), queryTiemStamp(cursor));
    }

    public static void addRelation(Context context, long eventId, long thoughtId) {
        ContentValues values = new ContentValues();
        values.put(EventThoughtRelationTable.EVENT_ID, eventId);
        values.put(EventThoughtRelationTable.THOUGHT_ID, thoughtId);
        DatabaseUtils.add(context, EventThoughtRelationTable.NAME, values);
    }

    private static void deleteRelation(Context context, long thoughtId) {
        DatabaseUtils.delete(
                context, EventThoughtRelationTable.NAME,
                EventThoughtRelationTable.THOUGHT_ID + "=?",
                new String[]{String.valueOf(thoughtId)});
    }

    public static long[] getThoughtIdsByEventId(Context context, long eventId) {
        Cursor cursor = DatabaseUtils.query(context, EventThoughtRelationTable.NAME,
                new String[] {EventThoughtRelationTable.THOUGHT_ID},
                EventThoughtRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(eventId)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return new long[] {};
        }
        long[] ret = new long[cursor.getCount()];
        while (cursor.moveToNext()) {
            ret[cursor.getPosition()] = queryId(cursor);
        }
        cursor.close();
        return ret;
    }

    public static Thoughts.Thought getThoughtById(Context context, long thoughtId) {
        Cursor cursor = DatabaseUtils.query(
                context, ThoughtsTable.NAME,
                new String[]{ThoughtsTable.ID, ThoughtsTable.THOUGHT, ThoughtsTable.TIMESTAMP},
                ThoughtsTable.ID + "=?",
                new String[]{String.valueOf(thoughtId)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Thoughts.Thought ret = queryThought(cursor);
        cursor.close();
        return ret;
    }

    public static Thoughts getThoughtsByEventId(Context context, long eventId) {
        long[] ids = getThoughtIdsByEventId(context, eventId);
        Thoughts ret = new Thoughts();
        for (long id : ids) {
            Thoughts.Thought t = getThoughtById(context, id);
            if (t != null) {
                ThoughtRes res = getRes(context, t.getKey());
                if (res != null && res.getResType() != Thoughts.Thought.HAS_NO_RES) {
                    t.setTypeAndPath(res.getResType(), res.getPath());
                }
                ret.add(t);
            }
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sorting = sp.getString("time_axis_sorting", "0");
        if (sorting.equals("1")) ret.reverse();
        return ret;
    }

    public static int getTodayCount(Context context) {
        Cursor cursor = DatabaseUtils.query(
                context, ThoughtsTable.NAME,
                new String[]{ThoughtsTable.ID, ThoughtsTable.TIMESTAMP},
                ThoughtsTable.TIMESTAMP + ">?",
                new String[]{String.valueOf(TimeUtils.getTodayMillis())});
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getEventCount(Context context, long eventId) {
        // TODO: 15/11/11 use count
        Thoughts t = getThoughtsByEventId(context, eventId);
        return t.length();
    }

    public static void addThought(Context context, long eventId, String thought, int resType, String path) {
        ContentValues values = new ContentValues();
        values.put(ThoughtsTable.THOUGHT, thought);
        values.put(ThoughtsTable.TIMESTAMP, System.currentTimeMillis());
        long id = DatabaseUtils.add(context, ThoughtsTable.NAME, values);
        addRes(context, id, resType, path);
        addRelation(context, eventId, id);
    }

    public static void updateThought(Context context, long thoughtId, String thought) {
        ContentValues values = new ContentValues();
        values.put(ThoughtsTable.THOUGHT, thought);
        DatabaseUtils.update(
                context, ThoughtsTable.NAME, values,
                ThoughtsTable.ID + "=",
                new String[]{String.valueOf(thoughtId)});
    }

    public static void addRes(Context context, long thoughtId, int resType, String path) {
        if (resType == Thoughts.Thought.HAS_NO_RES) return;
        ContentValues values = new ContentValues();
        values.put(ThoughtResTable.THOUGHT_ID, thoughtId);
        values.put(ThoughtResTable.TYPE, resType);
        values.put(ThoughtResTable.PATH, path);
        DatabaseUtils.add(context, ThoughtResTable.NAME, values);
    }

    private static void deleteRes(Context context, long thoughtId) {
        DatabaseUtils.delete(
                context, ThoughtResTable.NAME,
                ThoughtResTable.THOUGHT_ID + "=?",
                new String[]{String.valueOf(thoughtId)});
    }

    private static ThoughtRes getRes(Context context, long thoughtId) {
        Cursor cursor = DatabaseUtils.query(
                context, ThoughtResTable.NAME,
                new String[] {ThoughtResTable.ID, ThoughtResTable.THOUGHT_ID, ThoughtResTable.TYPE, ThoughtResTable.PATH},
                ThoughtResTable.THOUGHT_ID + "=?",
                new String[] {String.valueOf(thoughtId)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        ThoughtRes ret =  new ThoughtRes(
                DatabaseUtils.getLong(cursor, ThoughtResTable.ID),
                DatabaseUtils.getInt(cursor, ThoughtResTable.TYPE),
                DatabaseUtils.getString(cursor, ThoughtResTable.PATH),
                DatabaseUtils.getLong(cursor, ThoughtResTable.THOUGHT_ID));
        cursor.close();
        return ret;
    }

    public static void updateRes(Context context, long thoughtId, int resType, String path) {
        ContentValues values = new ContentValues();
        values.put(ThoughtResTable.TYPE, resType);
        values.put(ThoughtResTable.PATH, path);
        DatabaseUtils.update(
                context, ThoughtResTable.NAME, values,
                ThoughtResTable.THOUGHT_ID + "=",
                new String[] {String.valueOf(thoughtId)});
    }

    public static void deleteByThoughtId(Context context, long thoughtId) {
        deleteRelation(context, thoughtId);
        DatabaseUtils.delete(
                context, ThoughtsTable.NAME,
                ThoughtsTable.ID + "=?",
                new String[] {String.valueOf(thoughtId)});
        deleteRes(context, thoughtId);
    }

    public static void deleteByEventId(Context context, long eventId) {
        for (long t : getThoughtIdsByEventId(context, eventId)) {
            deleteByThoughtId(context, t);
        }
    }
}
