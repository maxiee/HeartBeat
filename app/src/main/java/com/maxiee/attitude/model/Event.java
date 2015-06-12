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
    private int timestamp;

    public Event(
            String mEvent,
            JSONArray mThoughts,
            JSONArray mLabels,
            int timestamp) {
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

    public int getTimestamp() {
        return timestamp;
    }
}
