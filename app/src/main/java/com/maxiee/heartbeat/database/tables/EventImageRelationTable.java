package com.maxiee.heartbeat.database.tables;

/**
 * Created by maxiee on 15-6-28.
 */
public class EventImageRelationTable {
    public static final String NAME = "event_image_relation";

    public static final String ID = "id";

    public static final String EVENT_ID = "event_id";

    public static final String IMAGE_ID = "image_id";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + EVENT_ID + " integer,"
            + IMAGE_ID + " integer"
            + ");";
}
