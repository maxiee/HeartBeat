package com.maxiee.heartbeat.database.tables;

/**
 * Created by maxiee on 15-9-8.
 */
public class ThoughtResTable {

    public static final String NAME = "thought_res";

    public static final String ID = "id";

    public static final String TYPE = "type";

    public static final String PATH = "path";

    public static final String THOUGHT_ID = "thought_id";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + TYPE + " integer,"
            + PATH + " text,"
            + THOUGHT_ID + " integer"
            + ");";
}
