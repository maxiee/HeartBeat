package com.maxiee.attitude.database.tables;

/**
 * Created by maxiee on 15-6-28.
 */
public class ImageTable {
    public static final String NAME = "images";

    public static final String ID = "id";

    public static final String URI = "uri";

    public static final String CREATE = "create table " + NAME
            + "("
            + ID + " integer primary key autoincrement,"
            + URI + " text"
            + ");";
}
