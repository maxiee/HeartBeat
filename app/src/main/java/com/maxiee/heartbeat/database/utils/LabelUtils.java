package com.maxiee.heartbeat.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.DatabaseHelper;
import com.maxiee.heartbeat.database.tables.EventLabelRelationTable;
import com.maxiee.heartbeat.database.tables.LabelsTable;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Label;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by maxiee on 15/11/10.
 */
public class LabelUtils {

    private static long queryId(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, LabelsTable.ID);
    }

    private static String queryLabelStr(Cursor cursor) {
        return DatabaseUtils.getString(cursor, LabelsTable.LABEL);
    }

    private static Label queryLabel(Cursor cursor) {
        return new Label(queryId(cursor), queryLabelStr(cursor));
    }

    private static long[] getRelativedIds(Context context, String idField, String getField, long id) {
        Cursor cursor = DatabaseUtils.query(
                context, EventLabelRelationTable.NAME,
                new String[]{EventLabelRelationTable.EVENT_ID, EventLabelRelationTable.LABEL_ID},
                idField + "=?",
                new String[]{String.valueOf(id)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return new long[] {};
        }
        long[] ret = new long[cursor.getCount()];
        while (cursor.moveToNext()) {
            ret[cursor.getPosition()] = DatabaseUtils.getLong(cursor, getField);
        }
        cursor.close();
        return ret;
    }

    public static long[] getRelativedEventIds(Context context, Label label) {
        return getRelativedIds(
                context,
                EventLabelRelationTable.LABEL_ID,
                EventLabelRelationTable.EVENT_ID,
                label.getId());
    }

    public static long[] getRelativedLabelIds(Context context, Event event) {
        return getRelativedIds(
                context,
                EventLabelRelationTable.EVENT_ID,
                EventLabelRelationTable.LABEL_ID,
                event.getId());
    }

    public static void addRelation(Context context, long eventId, long labelId) {
        ContentValues values = new ContentValues();
        values.put(EventLabelRelationTable.EVENT_ID, eventId);
        values.put(EventLabelRelationTable.LABEL_ID, labelId);
        DatabaseUtils.add(context, EventLabelRelationTable.NAME, values);
    }

    public static void deleteRelation(Context context, long eventId, long labelId) {
        DatabaseUtils.delete(
                context, EventLabelRelationTable.NAME,
                EventLabelRelationTable.EVENT_ID + "=? and " + EventLabelRelationTable.LABEL_ID + "=?",
                new String[]{String.valueOf(eventId), String.valueOf(labelId)});
        deleteIfNoUse(context, new Label(labelId, ""));
    }

    public static void deleteRelation(Context context, Event event) {
        long[] ids = getRelativedLabelIds(context, event);
        for (long id : ids) deleteRelation(context, event.getId(), id);
        for (long id : ids) deleteIfNoUse(context, new Label(id, ""));
    }

    public static Label getLabelByLabelId(Context context, long labelId) {
        Cursor cursor = DatabaseUtils.query(
                context, LabelsTable.NAME,
                new String[]{LabelsTable.ID, LabelsTable.LABEL},
                LabelsTable.ID + "=?",
                new String[]{String.valueOf(labelId)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Label l = queryLabel(cursor);
        cursor.close();
        return l;
    }

    public static ArrayList<Label> getLabelsByEvent(Context context, Event event) {
        long[] ids = getRelativedLabelIds(context, event);
        ArrayList<Label> labels = new ArrayList<>();
        for (long id : ids) {
            Label l = getLabelByLabelId(context, id);
            if (l != null) labels.add(l);
        }
        return labels;
    }

    public static ArrayList<Label> getAll(Context context) {
        Cursor cursor = DatabaseUtils.queryAll(
                context, LabelsTable.NAME,
                new String[]{LabelsTable.ID, LabelsTable.LABEL});
        ArrayList<Label> ret = new ArrayList<>();
        if (cursor.getCount() < 1) {
            cursor.close();
            return ret;
        }
        while (cursor.moveToNext()) {
            ret.add(queryLabel(cursor));
        }
        cursor.close();
        return ret;
    }

    public static Label addLabel(Context context, String label) {
        ContentValues values = new ContentValues();
        values.put(LabelsTable.LABEL, label);
        return new Label(DatabaseUtils.add(context, LabelsTable.NAME, values), label);
    }

    public static Label addLabel(Context context, long eventId, String label) {
        Label ret = addLabel(context, label);
        addRelation(context, eventId, ret.getId());
        return ret;
    }

    public static ArrayList<Label> addLabels(Context context, ArrayList<String> labels) {
        ArrayList<Label> ret = new ArrayList<>();
        for (String l : labels) {
            long id = hasLabel(context, l);
            if (id == NOT_FOUND) {
                ret.add(addLabel(context, l));
            } else {
                ret.add(getLabelByLabelId(context, id));
            }
        }
        return ret;
    }

    public static ArrayList<Label> addLabels(Context context, long eventId, ArrayList<String> labels) {
        ArrayList<Label> ret = addLabels(context, labels);
        for (Label l: ret) {
            addRelation(context, eventId, l.getId());
        }
        return ret;
    }

    public static void deleteIfNoUse(Context context, Label label) {
        ArrayList<Event> events = EventUtils.getEvents(context, label);
        if (!events.isEmpty()) return;
        DatabaseUtils.delete(
                context, LabelsTable.NAME,
                LabelsTable.ID + "=?",
                new String[] {String.valueOf(label.getId())});
    }

    public static Map<Long, Integer> getFreq(Context context) {
        ArrayList<Label> labels = LabelUtils.getAll(context);
        Map<Long, Integer> ret = new TreeMap<>();
        for (Label label : labels) {
            int count = (int) android.database.DatabaseUtils.queryNumEntries(
                        DatabaseHelper.instance(context).getReadableDatabase(),
                        EventLabelRelationTable.NAME,
                        EventLabelRelationTable.LABEL_ID + "=?",
                        new String[]{String.valueOf(label.getId())});
            ret.put(label.getId() ,count);
        }
        return ret;
    }

    public final static int NOT_FOUND = -1;

    // TODO return Label
    public static long hasLabel(Context context, String label) {
        Cursor cursor = DatabaseUtils.query(
                context, LabelsTable.NAME,
                new String[] {LabelsTable.ID, LabelsTable.LABEL},
                LabelsTable.LABEL + "=?",
                new String[] {label});
        if (cursor.getCount() < 1) {
            cursor.close();
            return -1;
        } else {
            cursor.moveToFirst();
            long ret = queryId(cursor);
            cursor.close();
            return ret;
        }
    }
}
