package com.maxiee.heartbeat.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by maxiee on 16/1/10.
 */
public interface IProviderEventDelegate {
    Cursor dispatchQuery(int code, Context context, Uri uri);
    Cursor random(Context context);
    Cursor byId(Context context, long id);
}
