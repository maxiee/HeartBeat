package com.maxiee.attitude.database.tables;

/**
 * Created by maxiee on 15-6-21.
 */
public class ThoughtsTable {

    public static final String NAME = "thoughts";

    public static final String ID = "id";

    public static final String THOUGHT = "thought";

    public static final String TIMESTAMP = "timestamp";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + THOUGHT + " text,"
            + TIMESTAMP + " integer"
            + ");";
}
