package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.maxiee.attitude.database.tables.EventsTable;
import com.maxiee.attitude.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maxiee on 15-6-14.
 */
public class AddThoughtApi extends BaseDBApi{

    private int mEventKey;
    private String mThought;
    private Context mContext;

    public AddThoughtApi(Context context,
                         final int eventKey,
                         final String thought) {
        super(context);
        mContext = context;
        mEventKey = eventKey;
        mThought = thought;
    }

    public boolean exec() throws JSONException {
        Event event = new GetOneEventApi(mContext, mEventKey).exec();
        event.addThought(mThought);

        ContentValues values = new ContentValues();
        values.put(EventsTable.THOUGHTS, event.getmThoughts().toString());

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        db.update(
                EventsTable.NAME,
                values,
                EventsTable.ID + " =?",
                new String[] {String.valueOf(mEventKey)}
        );
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }
}
