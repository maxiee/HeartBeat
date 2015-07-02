package com.maxiee.heartbeat.database.tables;

/**
 * Created by maxiee on 15-6-11.
 */
public class LabelsTable {
    public static final String NAME = "labels";

    public static final String ID = "id";

    public static final String LABEL = "label";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + LABEL + " text"
            + ");";
}
