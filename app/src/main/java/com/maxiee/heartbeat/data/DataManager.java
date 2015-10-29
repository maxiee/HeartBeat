package com.maxiee.heartbeat.data;

import android.content.Context;
import android.util.Log;

import com.maxiee.heartbeat.database.api.DeleteEventByKeyApi;
import com.maxiee.heartbeat.database.api.DeleteImageByEventKeyApi;
import com.maxiee.heartbeat.database.api.DeleteThoughtsByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetAllEventApi;
import com.maxiee.heartbeat.database.api.GetOneEventApi;
import com.maxiee.heartbeat.database.api.GetThoughtTodayCountApi;
import com.maxiee.heartbeat.database.api.GetTodayEventApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.EventListAdapter;
import com.maxiee.heartbeat.ui.adapter.TodayEventAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15/10/28.
 */
public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();

    private Context mContext;
    private ArrayList<Event> mEventList;
    private EventListAdapter mEventAdapter;
    private ArrayList<Event> mTodayList;
    private TodayEventAdapter mTodayAdapter;
    private int mCountTodayEvent;
    private int mCountTodayThought;
    private static DataManager mInstance;

    public static DataManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataManager(context.getApplicationContext());
        }
        return mInstance;
    }

    private DataManager(Context context) {
        mContext = context;
        mEventList = new GetAllEventApi(context).exec();
        mEventAdapter = new EventListAdapter(mEventList);
        mTodayList = new GetTodayEventApi(context).exec();
        mTodayAdapter = new TodayEventAdapter(mTodayList);
        countTodayEvent();
        countTodayThought();
    }

    public void reload() {
        mEventList.clear();
        mTodayList.clear();
        mEventList = new GetAllEventApi(mContext).exec();
        mTodayList = new GetTodayEventApi(mContext).exec();
        mEventAdapter.setData(mEventList);
        mTodayAdapter.setData(mTodayList);
        countTodayEvent();
        countTodayThought();
    }

    public EventListAdapter getEventAdapter() {
        return mEventAdapter;
    }

    public TodayEventAdapter getTodayAdapter() {
        return mTodayAdapter;
    }

    public boolean isEventEmpty() {
        return mEventList.isEmpty();
    }

    public boolean isTodayEmpty() {
        return mTodayList.isEmpty();
    }

    private void countTodayEvent() {
        mCountTodayEvent = mTodayList.size();
    }

    private void countTodayThought() {
        mCountTodayThought = new GetThoughtTodayCountApi(mContext).exec();
    }

    public String getTodayEventCountString() {
        return String.valueOf(mCountTodayEvent);
    }

    public int getTodayThoughtCount() {
        return mCountTodayThought;
    }

    public String getTodayThoughtCountString() {
        return String.valueOf(mCountTodayThought);
    }

    public void notifyDataSetChanged() {
        countTodayEvent();
        countTodayThought();
        mEventAdapter.notifyDataSetChanged();
        mTodayAdapter.notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        mEventList.add(0, event);
        mTodayList.add(0, event);
    }

    public void deleteEvent(int key) {
        new DeleteEventByKeyApi(mContext, key).exec();
        new DeleteImageByEventKeyApi(mContext, key).exec();
        new DeleteThoughtsByEventKeyApi(mContext, key).exec();
        int indexEvent = findFromList(key, mEventList);
        if (indexEvent >= 0) mEventList.remove(indexEvent);
        int indexToday = findFromList(key, mTodayList);
        if (indexToday >= 0) mTodayList.remove(indexToday);
        notifyDataSetChanged();
    }

    public void updateEvent(int key) {
        int indexEvent = findFromList(key, mEventList);
        int indexToday = findFromList(key, mTodayList);

        if (indexEvent == -1 && indexToday == -1) {
            return;
        }

        Event e = new GetOneEventApi(mContext, key).exec();

        if (e == null) {
            return;
        }

        if (indexEvent >= 0) mEventList.set(indexEvent, e);
        if (indexToday >= 0) mTodayList.set(indexToday, e);
    }

    private static int findFromList(int key, ArrayList<Event> list) {
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).getmId() == key) {
                return i;
            }
        }
        return -1;
    }

    public void logInfo() {
        Log.d(TAG, "EventList size:" + String.valueOf(mEventList.size()));
        Log.d(TAG, "TodayList size:" + String.valueOf(mTodayList.size()));
    }
}