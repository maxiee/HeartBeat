package com.maxiee.attitude.database.tables;

/**
 * Created by maxiee on 15-6-17.
 */
public class EventLabelRelationTable {
    public static final String NAME = "event_label_ralation";

    public static final String ID = "id";

    public static final String EVENT_ID = "event_id";

    public static final String LABEL_ID = "label_id";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + EVENT_ID + " integer,"
            + LABEL_ID + " integer"
            + ");";
}
