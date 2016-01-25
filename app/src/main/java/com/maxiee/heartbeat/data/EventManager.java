package com.maxiee.heartbeat.data;

import android.content.Context;

import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.model.DayCard;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.support.StopWatch;

import java.util.ArrayList;

/**
 * Created by maxiee on 15/11/10.
 */
public class EventManager extends BaseEventManager{
    public static final String TAG = EventManager.class.getSimpleName();

    private ArrayList<DayCard> mData = new ArrayList<>();

    public EventManager(Context context) {
        super(context);
        reload();
    }

    @Override
    public void reload() {
        if (mEventList != null) mEventList.clear();
        mEventList = EventUtils.getAllEvents(mContext);
        reloadDayCardData();
    }

    public void reloadDayCardData() {
        StopWatch stopWatch = new StopWatch(TAG, "reloadDayCardData use time");
        mData.clear();
        parseData(mEventList);
        stopWatch.stop();
    }

    private void parseData(ArrayList<Event> eventList) {
        long dayStart = Long.MIN_VALUE;
        DayCard dayCard = null;
        for (Event e : eventList) {
            if (dayCard == null) {
                dayStart = TimeUtils.getDayStart(e.getTimestamp());
                dayCard = new DayCard(dayStart, e);
                continue;
            }
            if (TimeUtils.isInSameDay(dayStart, e.getTimestamp())) {
                dayCard.addEvent(e);
            } else {
                mData.add(dayCard);
                dayStart = TimeUtils.getDayStart(e.getTimestamp());
                dayCard = new DayCard(dayStart, e);
            }
        }
        if (dayCard != null) mData.add(dayCard);
    }

    public ArrayList<DayCard> getDayCardData() {return mData;}
}
