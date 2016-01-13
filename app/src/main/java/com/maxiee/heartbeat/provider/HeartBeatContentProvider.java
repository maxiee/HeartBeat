package com.maxiee.heartbeat.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by maxiee on 16/1/9.
 */
public class HeartBeatContentProvider extends ContentProvider {
    private UriMatcher mMatcher;
    private IProviderEventDelegate mEventDelegate;

    @Override
    public boolean onCreate() {
        initMatcher();
        mEventDelegate = new ProviderEventDelegate();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = mMatcher.match(uri);
        Cursor cursor = null;
        cursor = mEventDelegate.dispatchQuery(code, getContext());
        if (cursor != null) return cursor;
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void initMatcher() {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(Constant.BASE_URI, Constant.API_EVENT_RANDOM, Constant.API_EVENT_RANDOM_CODE);
    }
}
