package com.maxiee.heartbeat.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;
import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Label;

import java.util.ArrayList;

/**
 * Created by maxiee on 15/11/10.
 */
public class EventUtils {
    public static final String TAG = EventUtils.class.getSimpleName();

    public static Event addEvent(Context context, String content, long timestamp) {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, content);
        values.put(EventsTable.TIMESTAMP, timestamp);
        long key = DatabaseUtils.add(context, EventsTable.NAME, values);
        return new Event(key, content, timestamp);
    }

    public static void updateEvent(Context context, Event event) {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, event.getEvent());
        values.put(EventsTable.TIMESTAMP, event.getTimestamp());
        DatabaseUtils.update(
                context, EventsTable.NAME,
                values, EventsTable.ID + "=?",
                new String[] {String.valueOf(event.getId())});
    }

    private static long queryId(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, EventsTable.ID);
    }

    private static String queryContent(Cursor cursor) {
        return DatabaseUtils.getString(cursor, EventsTable.EVENT);
    }

    private static long queryTimeStamp(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, EventsTable.TIMESTAMP);
    }

    private static Event queryEvent(Cursor cursor) {
        return new Event(queryId(cursor), queryContent(cursor), queryTimeStamp(cursor));
    }

    public static Event getEvent(Context context, long id) {
        Cursor cursor = DatabaseUtils.query(
                context, EventsTable.NAME,
                new String[]{EventsTable.ID, EventsTable.EVENT, EventsTable.TIMESTAMP},
                EventsTable.ID + "=?",
                new String[] {String.valueOf(id)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Event e = queryEvent(cursor);
        cursor.close();
        return e;
    }

    public static ArrayList<Event> getToday(Context context) {
        Cursor cursor = DatabaseUtils.queryOrderDesc(
                context, EventsTable.NAME,
                new String[]{EventsTable.ID, EventsTable.EVENT, EventsTable.TIMESTAMP},
                EventsTable.TIMESTAMP + ">?",
                new String[] {String.valueOf(TimeUtils.getTodayMillis())},
                EventsTable.TIMESTAMP);
        ArrayList<Event> ret = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return ret;
        }
        while (cursor.moveToNext()) {
            ret.add(queryEvent(cursor));
        }
        cursor.close();
        return ret;
    }

    public static ArrayList<Event> getEvents(Context context, Label label) {
        final String rawSQL =
                "SELECT " +  EventsTable.NAME + "." + EventsTable.ID + ", " + EventsTable.EVENT + ", " + EventsTable.TIMESTAMP + ", "
                + EventLabelRelationTable.LABEL_ID + " FROM " + EventsTable.NAME + " JOIN " + EventLabelRelationTable.NAME
                + " ON " + EventsTable.NAME + "." + EventsTable.ID + "=" + EventLabelRelationTable.NAME + "." + EventLabelRelationTable.EVENT_ID
                + " WHERE " + EventLabelRelationTable.LABEL_ID + "=?"
                + " ORDER BY " + EventsTable.TIMESTAMP + " DESC;";
        Cursor cursor = DatabaseUtils.getReadableDatabase(context).rawQuery(
                rawSQL,
                new String[] { String.valueOf(label.getId())});
        ArrayList<Event> ret = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return ret;
        }
        while (cursor.moveToNext()) {
            ret.add(queryEvent(cursor));
        }
        cursor.close();
        return ret;
    }

    public static ArrayList<Event> getAllEvents(Context context) {
        Cursor cursor = DatabaseUtils.queryAllOrderDesc(
                context, EventsTable.NAME,
                new String[]{EventsTable.ID, EventsTable.EVENT, EventsTable.TIMESTAMP},
                EventsTable.TIMESTAMP);
        ArrayList<Event> eventList = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return eventList;
        }
        while (cursor.moveToNext()) {
            eventList.add(queryEvent(cursor));
        }
        cursor.close();
        return eventList;
    }

    public static void deleteEvent(Context context, long id) {
        DatabaseUtils.delete(
                context, EventsTable.NAME,
                EventsTable.ID + "=?",
                new String[] {String.valueOf(id)});
        LabelUtils.deleteRelation(context, new Event(id, "", 0));
    }

    public static ArrayList<Event> searchEvent(Context context, String search) {
        Cursor cursor = DatabaseUtils.query(
                context, EventsTable.NAME,
                new String[] {EventsTable.ID, EventsTable.EVENT, EventsTable.TIMESTAMP},
                EventsTable.EVENT + " like ?",
                new String[] {"%" + search + "%"});
        ArrayList<Event> ret = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return ret;
        }
        while (cursor.moveToNext()) {
            ret.add(0, queryEvent(cursor));
        }
        cursor.close();
        return ret;
    }
}
