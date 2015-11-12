package com.maxiee.heartbeat.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.maxiee.heartbeat.database.tables.EventImageRelationTable;
import com.maxiee.heartbeat.database.tables.ImageTable;
import com.maxiee.heartbeat.model.Image;

/**
 * Created by maxiee on 15/11/10.
 */
public class ImageUtils {

    private static long queryImageIdByRelation(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, EventImageRelationTable.IMAGE_ID);
    }

    private static long queryIamgeId(Cursor cursor) {
        return DatabaseUtils.getLong(cursor, ImageTable.ID);
    }

    private static String queryPath(Cursor cursor) {
        return DatabaseUtils.getString(cursor, ImageTable.URI);
    }

    private static Image queryImage(Cursor cursor) {
        return new Image(queryIamgeId(cursor), queryPath(cursor));
    }

    private static void addRelation(Context context, long eventId, Image image) {
        ContentValues values = new ContentValues();
        values.put(EventImageRelationTable.EVENT_ID, eventId);
        values.put(EventImageRelationTable.IMAGE_ID, image.getId());
        DatabaseUtils.add(context, EventImageRelationTable.NAME, values);
    }

    private static long[] getRelation(Context context, long eventId) {
        Cursor cursor = DatabaseUtils.query(
                context, EventImageRelationTable.NAME,
                new String[] {EventImageRelationTable.IMAGE_ID},
                EventImageRelationTable.EVENT_ID + "=?",
                new String[] {String.valueOf(eventId)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return new long[] {};
        }
        long[] imageIds = new long[cursor.getCount()];
        while (cursor.moveToNext()) {
            imageIds[cursor.getPosition()] = queryImageIdByRelation(cursor);
        }
        cursor.close();
        return imageIds;
    }

    private static void deleteRelation(Context context, long imageId) {
        DatabaseUtils.delete(
                context, EventImageRelationTable.NAME,
                EventImageRelationTable.IMAGE_ID + "=?",
                new String[]{String.valueOf(imageId)});
    }

    public static Image addImage(Context context, long eventId, String path) {
        ContentValues values = new ContentValues();
        values.put(ImageTable.URI, path);
        long imageID = DatabaseUtils.add(context, ImageTable.NAME, values);
        Image image = new Image(imageID, path);
        addRelation(context, eventId, image);
        return image;
    }

    public static Image getImageByImageId(Context context, long imageId) {
        Cursor cursor = DatabaseUtils.query(
                context, ImageTable.NAME,
                new String[]{ImageTable.ID, ImageTable.URI},
                ImageTable.ID + "=?",
                new String[]{String.valueOf(imageId)});
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        Image i =  queryImage(cursor);
        cursor.close();
        return i;
    }

    public static Image getImageByEventId(Context context, long eventId) {
        long[] imageIds = getRelation(context, eventId);
        if (imageIds.length < 1) {return null;}
        return getImageByImageId(context, imageIds[0]);
    }

    public static void updateImageByEventId(Context context, long eventId, String path) {
        Image i = getImageByEventId(context, eventId);
        if (i == null) return;
        ContentValues values = new ContentValues();
        values.put(ImageTable.URI, path);
        DatabaseUtils.update(
                context, ImageTable.NAME, values,
                ImageTable.ID + "=?", new String[] {String.valueOf(i.getId())});
    }

    public static void deleteByImageId(Context context, long imageId) {
        DatabaseUtils.delete(
                context, ImageTable.NAME,
                ImageTable.ID + "=?",
                new String[]{String.valueOf(imageId)});
    }

    public static void deleteByEventId(Context context, long eventId) {
        long[] imageIds = getRelation(context, eventId);
        for (long imageId : imageIds) {
            deleteRelation(context, imageId);
            deleteByImageId(context, imageId);
        }
    }
}
