package com.maxiee.attitude.common;

import android.content.Context;
import android.text.format.DateFormat;

import com.maxiee.attitude.R;

import java.util.Calendar;

/**
 * Created by maxiee on 15-6-14.
 */
public class TimeUtils {

    private static final int MS_ONE_SEC = 1000;
    private static final int MS_ONE_MIN = 60 * MS_ONE_SEC;
    private static final int MS_ONE_HOUR = MS_ONE_MIN * 60;
    public static final int MS_ONE_DAY = MS_ONE_HOUR * 24;
    private static final int MS_TEN_DAYS = MS_ONE_DAY * 10;

    public static String parseTime(final Context context, final long timestamp) {
        Long timeNow = System.currentTimeMillis();
        Long delta = timeNow - timestamp;

        if (delta > MS_TEN_DAYS) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            return DateFormat.format("yyyy-MM-dd", cal).toString();
        }

        long count = 0;
        String timeUnit = "";

        if (delta < MS_ONE_HOUR) {
            count = delta/ MS_ONE_MIN;
            timeUnit = context.getString(R.string.minute_ago);
        } else if (delta < MS_ONE_DAY) {
            count = delta/ MS_ONE_HOUR;
            timeUnit = context.getString(R.string.hour_ago);
        } else if (delta < MS_TEN_DAYS) {
            count = delta/ MS_ONE_DAY;
            timeUnit = context.getString(R.string.day_ago);
        }
        return String.valueOf(count) + timeUnit;
    }
}
