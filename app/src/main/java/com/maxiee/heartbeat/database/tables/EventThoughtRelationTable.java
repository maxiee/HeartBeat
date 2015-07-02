package com.maxiee.heartbeat.database.tables;

/**
 * Created by maxiee on 15-6-21.
 */
public class EventThoughtRelationTable {

    public static final String NAME = "event_thought_ralation";

    public static final String ID = "id";

    public static final String EVENT_ID = "event_id";

    public static final String THOUGHT_ID = "thought_id";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + EVENT_ID + " integer,"
            + THOUGHT_ID + " integer"
            + ");";
}
