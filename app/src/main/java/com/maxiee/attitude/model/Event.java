package com.maxiee.attitude.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by maxiee on 15-6-12.
 */
public class Event {
    private String mEvent;
    private Thoughts mThoughts;
    private JSONArray mLabels;
    private long timestamp;
    private int mId;

    public Event(
            int id,
            String mEvent,
            JSONArray thoughts,
            JSONArray labels,
            long timestamp) {
        this.mId = id;
        this.mEvent = mEvent;
        this.mThoughts = new Thoughts(thoughts);
        this.mLabels = labels;
        this.timestamp = timestamp;
    }

    public void addThought(String thought) throws JSONException {
        mThoughts.addThought(thought);
    }


    public String getmEvent() {
        return mEvent;
    }

    public Thoughts getmThoughts() {
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
