package com.maxiee.heartbeat.data;

import android.content.Context;

import com.maxiee.heartbeat.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15/11/10.
 */
abstract class BaseEventManager {

    protected Context mContext;
    protected ArrayList<Event> mEventList;

    public BaseEventManager(Context context) {
        mContext = context;
    }

    abstract void reload();

    public ArrayList<Event> getEvents() {
        return mEventList;
    }

    public void addEvent(Event event) {
        for (int i=0; i<mEventList.size(); i++) {
            Event e = mEventList.get(i);
            if (event.getTimestamp() > e.getTimestamp()) {
                mEventList.add(i, event);
                return;
            }
        }
        mEventList.add(event);
    }

    public void deleteEvent(long key) {
        int indexEvent = findFromList(key, mEventList);
        if (indexEvent >= 0) mEventList.remove(indexEvent);
    }

    public void updateEvent(Event e) {
        int indexEvent = findFromList(e.getId(), mEventList);
        mEventList.remove(indexEvent);
        addEvent(e);
    }

    public boolean isEmpty() {
        return mEventList.isEmpty();
    }

    public int size() {
        return mEventList.size();
    }

    private static int findFromList(long key, ArrayList<Event> list) {
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).getId() == key) {
                return i;
            }
        }
        return -1;
    }
}
