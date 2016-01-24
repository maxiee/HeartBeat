package com.maxiee.heartbeat.data;

import android.content.Context;
import android.util.Log;

import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.DayCardEventAdapter;
import com.maxiee.heartbeat.ui.adapter.TodayEventAdapter;

/**
 * Created by maxiee on 15/10/28.
 */
public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();

    private Context mContext;
    private EventManager mEventManager;
    private TodayManager mTodayManager;
//    private EventListAdapter mEventAdapter;
    private DayCardEventAdapter mEventAdapter;
    private TodayEventAdapter mTodayAdapter;
    private int mToday;
    private static DataManager mInstance;

    public static DataManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataManager(context.getApplicationContext());
        }
        return mInstance;
    }

    private DataManager(Context context) {
        mContext = context;
        mEventManager = new EventManager(mContext);
        mTodayManager = new TodayManager(mContext);
//        mEventAdapter = new EventListAdapter(mEventManager.getEvents());
        mEventAdapter = new DayCardEventAdapter(mEventManager.getDayCardData());
        mTodayAdapter = new TodayEventAdapter(mTodayManager.getEvents());
        mToday = TimeUtils.getToday();
    }

    public void reload() {
        mEventManager.reload();
        mTodayManager.reload();
        mEventAdapter.setData(mEventManager.getDayCardData());
        mTodayAdapter.setData(mTodayManager.getEvents());
    }

    public EventManager getEventManager() {return  mEventManager;}

//    public EventListAdapter getEventAdapter() {
//        return mEventAdapter;
//    }

    public DayCardEventAdapter getEventAdapter() {
        return mEventAdapter;
    }

    public TodayEventAdapter getTodayAdapter() {
        return mTodayAdapter;
    }

    public boolean isEventEmpty() {
        return mEventManager.isEmpty();
    }

    public boolean isTodayEmpty() {
        return mTodayManager.isEmpty();
    }

    public int getTodayEventCount() {
        return mTodayManager.countTodayEvent();
    }

    public int getTodayThoughtCount() {
        return mTodayManager.countTodayThought();
    }

    public void notifyDataSetChanged() {
        mEventAdapter.notifyDataSetChanged();
        mTodayAdapter.notifyDataSetChanged();
    }

    public void addEvent(Event event) {
        // TODO move the code of DB-adding here
        mEventManager.addEvent(event);
        mTodayManager.addEvent(event);
        // TODO wtf
        mEventManager.reloadDayCardData();
    }

    public void deleteEvent(long key) {
        EventUtils.deleteEvent(mContext, key);
        ImageUtils.deleteByEventId(mContext, key);
        ThoughtUtils.deleteByEventId(mContext, key);
        mEventManager.deleteEvent(key);
        mTodayManager.deleteEvent(key);
        // TODO wtf
        mEventManager.reloadDayCardData();
        notifyDataSetChanged();
        checkNewDay();
    }

    public void updateEvent(long key) {
        Event e = EventUtils.getEvent(mContext, key);
        if (e == null) return;
        mEventManager.updateEvent(e);
        mTodayManager.updateEvent(e);
        // TODO wtf
        mEventManager.reloadDayCardData();
        checkNewDay();
    }

    public void checkNewDay() {
        int day = TimeUtils.getToday();
        if (day != mToday) {
            mToday = day;
            mTodayManager.reload();
            notifyDataSetChanged();
        }
    }

    public void logInfo() {
        Log.d(TAG, "EventList size:" + String.valueOf(mEventManager.size()));
        Log.d(TAG, "TodayList size:" + String.valueOf(mTodayManager.size()));
    }
}