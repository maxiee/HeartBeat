package com.maxiee.heartbeat.model;

/**
 * Created by maxiee on 15/11/10.
 */
public class Image {
    private long mId;
    private String mPath;

    public Image(long id, String path) {
        mId = id;
        mPath = path;
    }

    public long getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }
}
