package com.maxiee.heartbeat.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Label;

import java.util.ArrayList;

/**
 * Created by maxiee on 15/11/10.
 */
public class EventUtils {
    public static final String TAG = EventUtils.class.getSimpleName();

    public static Event addEvent(Context context, String content) {
        long timeStamp = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, content);
        values.put(EventsTable.TIMESTAMP, timeStamp);
        long key = DatabaseUtils.add(context, EventsTable.NAME, values);
        return new Event(key, content, timeStamp);
    }

    public static void updateEvent(Context context, Event event) {
        ContentValues values = new ContentValues();
        values.put(EventsTable.EVENT, event.getEvent());
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
        Cursor cursor = DatabaseUtils.query(
                context, EventsTable.NAME,
                new String[]{EventsTable.ID, EventsTable.EVENT, EventsTable.TIMESTAMP},
                EventsTable.TIMESTAMP + ">?",
                new String[] {String.valueOf(TimeUtils.getTodayMillis())});
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

    public static ArrayList<Event> getEvents(Context context, Label label) {
        long[] ids = LabelUtils.getRelativedEventIds(context, label);
        ArrayList<Event> ret = new ArrayList<>();
        for (long id : ids) {
            Event e = EventUtils.getEvent(context, id);
            ret.add(0, e);
        }
        return ret;
    }

    public static ArrayList<Event> getAllEvents(Context context) {
        Cursor cursor = DatabaseUtils.queryAll(
                context, EventsTable.NAME,
                new String[]{EventsTable.ID, EventsTable.EVENT, EventsTable.TIMESTAMP});
        ArrayList<Event> eventList = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return eventList;
        }
        while (cursor.moveToNext()) {
            eventList.add(0, queryEvent(cursor));
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
