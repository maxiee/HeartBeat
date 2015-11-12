package com.maxiee.heartbeat.data;

import android.content.Context;

import com.maxiee.heartbeat.database.utils.EventUtils;

/**
 * Created by maxiee on 15/11/10.
 */
public class EventManager extends BaseEventManager{

    public EventManager(Context context) {
        super(context);
        reload();
    }

    @Override
    public void reload() {
        if (mEventList != null) mEventList.clear();
        mEventList = EventUtils.getAllEvents(mContext);
    }
}
