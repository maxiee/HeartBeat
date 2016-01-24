package com.maxiee.heartbeat.model;

import java.util.ArrayList;

/**
 * Created by maxiee on 16/1/22.
 */
public class DayCard {
    private long mTimeStamp;
    private ArrayList<Event> mEventList;

    public DayCard (long timeStamp, Event event) {
        mTimeStamp = timeStamp;
        mEventList = new ArrayList<>();
        mEventList.add(event);
    }

    public void addEvent(Event event) {
        mEventList.add(event);
    }

    public long getTimeStamp() {return mTimeStamp;}

    public ArrayList<Event> getEventList() {return mEventList;}
}
