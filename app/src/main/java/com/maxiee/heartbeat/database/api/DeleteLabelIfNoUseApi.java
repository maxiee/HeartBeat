package com.maxiee.heartbeat.database.api;

import android.content.Context;
import android.util.Log;

import com.maxiee.heartbeat.database.tables.LabelsTable;
import com.maxiee.heartbeat.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-10.
 */
public class DeleteLabelIfNoUseApi extends BaseDBApi {
    private static final String TAG = DeleteLabelIfNoUseApi.class.getSimpleName();
    private int mLabelKey;

    public DeleteLabelIfNoUseApi(Context context,
                                 int labelKey) {
        super(context);
        mLabelKey = labelKey;
    }

    public void exec() {
        ArrayList<Event> events = new GetEventsByLabelKeyApi(mContext, mLabelKey).exec();
        if (events == null)  {
            Log.d(TAG, "delete nouse label_id: " + String.valueOf(mLabelKey));
            mDatabaseHelper.getWritableDatabase().delete(
                    LabelsTable.NAME,
                    LabelsTable.ID + "=?",
                    new String[] {String.valueOf(mLabelKey)}
            );
        }
    }
}
