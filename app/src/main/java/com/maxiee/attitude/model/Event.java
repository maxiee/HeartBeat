package com.maxiee.attitude.model;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class Event {
    private String mEvent;
    private JSONArray mThoughts;
    private JSONArray mLabels;
    private long timestamp;
    private int mId;

    public Event(
            int id,
            String mEvent,
            JSONArray mThoughts,
            JSONArray mLabels,
            long timestamp) {
        this.mId = id;
        this.mEvent = mEvent;
        this.mThoughts = mThoughts;
        this.mLabels = mLabels;
        this.timestamp = timestamp;
    }

    public String getmEvent() {
        return mEvent;
    }

    public JSONArray getmThoughts() {
        return mThoughts;
    }

    public JSONArray getmLabels() {
        return mLabels;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getmId() {
        return mId;
    }
}
