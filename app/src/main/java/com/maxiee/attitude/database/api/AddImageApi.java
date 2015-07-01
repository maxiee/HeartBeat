package com.maxiee.attitude.database.api;

import android.content.ContentValues;
import android.content.Context;

import com.maxiee.attitude.database.tables.ImageTable;

/**
 * Created by maxiee on 15-7-1.
 */
public class AddImageApi extends BaseDBApi{

    private int mEventKey;
    private String mImageUri;

    public AddImageApi(Context context,
                       final int eventKey,
                       final String uri) {
        super(context);
        mEventKey = eventKey;
        mImageUri = uri;
    }

    public boolean exec() {
        ContentValues values = new ContentValues();
        values.put(ImageTable.URI, mImageUri);

        int imageKey = (int) add(ImageTable.NAME, values);

        new AddEventImageRelationApi(
                mContext,
                mEventKey,
                imageKey
        ).exec();

        return true;
    }
}
