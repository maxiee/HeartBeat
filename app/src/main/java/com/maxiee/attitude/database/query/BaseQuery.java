package com.maxiee.attitude.database.query;

import android.content.Context;
import android.database.Cursor;

import com.maxiee.attitude.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-11.
 */
public abstract class BaseQuery {
    private DatabaseHelper databaseHelper;

    public BaseQuery(Context context) {
        databaseHelper = DatabaseHelper.instance(context);
    }

    protected abstract ArrayList<?> query();
}
