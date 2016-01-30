package com.maxiee.heartbeat.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by maxiee on 16/1/31.
 */
public interface IProviderThoughtDelegate {
    Cursor dispatchQuery(int code, Context context, Uri uri);
    Cursor getThought(Context context, long eventId);
    Cursor getThoughtRes(Context context, long thoughtId);
}
