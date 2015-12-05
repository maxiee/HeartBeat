package com.maxiee.heartbeat.data;

import android.content.Context;

import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;

import java.util.ArrayList;

/**
 * Created by maxiee on 15/11/10.
 */
public class TodayManager extends BaseEventManager{

    public TodayManager(Context context) {
        super(context);
        mEventList = new ArrayList<>();
        reload();
    }

    @Override
    void reload() {
        if (mEventList != null) mEventList.clear();
        mEventList.addAll(EventUtils.getToday(mContext));
    }

    public int countTodayThought() {
        return ThoughtUtils.getTodayCount(mContext);
    }

    public int countTodayEvent() {
       return mEventList.size();
    }
}
