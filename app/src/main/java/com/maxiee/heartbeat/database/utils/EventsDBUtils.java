package com.maxiee.heartbeat.database.utils;

import android.database.Cursor;

import com.google.android.agera.Function;
import com.google.android.agera.Receiver;
import com.maxiee.heartbeat.database.tables.EventsTable;
import com.maxiee.heartbeat.model.Event;

import static com.google.android.agera.database.SqlRequests.sqlDeleteRequest;
import static com.google.android.agera.database.SqlRequests.sqlInsertRequest;
import static com.google.android.agera.database.SqlRequests.sqlUpdateRequest;

/**
 * Created by maxiee on 16/4/23.
 */
public class EventsDBUtils {
    public static final String WHERE = EventsTable.ID + "=?";

    public static final String SQL_GET_ALL =
            "SELECT " + EventsTable.ID + ", " + EventsTable.EVENT + ", " + EventsTable.TIMESTAMP
            + " FROM " + EventsTable.NAME + " ORDER BY " + EventsTable.TIMESTAMP + " DESC";

    public static void insert(Receiver<Object> r, Event e) {
        r.accept(
                sqlInsertRequest()
                .table(EventsTable.NAME)
                .column(EventsTable.EVENT, e.getEvent())
                .column(EventsTable.TIMESTAMP, String.valueOf(e.getTimestamp()))
                .compile());
    }

    public static void delete(Receiver<Object> r, Event e) {
        r.accept(
                sqlDeleteRequest()
                .table(EventsTable.NAME)
                .where(WHERE)
                .arguments(String.valueOf(e.getId()))
                .compile());
        // LabelUtils.deleteRelation(context, new Event(id, "", 0));
    }

    public static void update(Receiver<Object> r, Event e) {
        r.accept(
                sqlUpdateRequest()
                .table(EventsTable.NAME)
                .column(EventsTable.EVENT, e.getEvent())
                .column(EventsTable.TIMESTAMP, String.valueOf(e.getTimestamp()))
                .where(WHERE)
                .arguments(String.valueOf(e.getId()))
                .compile());
    }

    public static Function<Cursor, Event> cursorToEvent() {
        return cursor -> new Event(
                cursor.getLong(cursor.getColumnIndex(EventsTable.ID)),
                cursor.getString(cursor.getColumnIndex(EventsTable.EVENT)),
                cursor.getLong(cursor.getColumnIndex(EventsTable.TIMESTAMP)));
    }
}
