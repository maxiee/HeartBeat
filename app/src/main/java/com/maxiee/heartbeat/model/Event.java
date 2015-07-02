package com.maxiee.heartbeat.model;

/**
 * Created by maxiee on 15-6-12.
 */
public class Event {
    private String mEvent;
    private long timestamp;
    private int mId;

    public Event(
            int id,
            String mEvent,
            long timestamp) {
        this.mId = id;
        this.mEvent = mEvent;
        this.timestamp = timestamp;
    }

    public String getmEvent() {
        return mEvent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getmId() {
        return mId;
    }
}
