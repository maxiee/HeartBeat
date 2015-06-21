package com.maxiee.attitude.database.tables;

/**
 * Created by maxiee on 15-6-11.
 */
public class EventsTable {
    public static final String NAME = "events";

    public static final String ID = "id";

    public static final String EVENT = "event";

    public static final String TIMESTAMP = "timestamp";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + EVENT + " text,"
            + TIMESTAMP + " integer"
            + ");";
}
