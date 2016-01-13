package com.maxiee.heartbeat.provider;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by maxiee on 16/1/10.
 */
public interface IProviderEventDelegate {
    Cursor dispatchQuery(int code, Context context);
    Cursor random(Context context);
}
