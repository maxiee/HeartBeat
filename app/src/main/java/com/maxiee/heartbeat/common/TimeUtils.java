package com.maxiee.heartbeat.common;

import android.content.Context;
import android.text.format.DateFormat;

import com.maxiee.heartbeat.R;

import java.util.Calendar;

/**
 * Created by maxiee on 15-6-14.
 */
public class TimeUtils {
    private final static int WEEK_COUNT = 7;
    private static final int MS_ONE_SEC = 1000;
    private static final int MS_ONE_MIN = 60 * MS_ONE_SEC;
    private static final int MS_ONE_HOUR = MS_ONE_MIN * 60;
    public static final int MS_ONE_DAY = MS_ONE_HOUR * 24;
    private static final int MS_TEN_DAYS = MS_ONE_DAY * 10;
    private static final int MS_FOUR_DAYS = MS_ONE_DAY * 4;

    public static String getDate(final Context context) {
        Calendar cal = Calendar.getInstance();
        return DateFormat.format("yyyy-MM-dd-h-mm", cal).toString();
    }

    public static String parseTime(final Context context, final long timestamp) {
        Long timeNow = System.currentTimeMillis();
        Long delta = timeNow - timestamp;

        if (delta > MS_FOUR_DAYS) {
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

    public static Calendar calendarDaysBefore(int days) {
        Calendar curDate = Calendar.getInstance();
        curDate.set(
                curDate.get(Calendar.YEAR),
                curDate.get(Calendar.MONTH),
                curDate.get(Calendar.DAY_OF_MONTH),
                0, 0
        );
        curDate.add(Calendar.DAY_OF_MONTH, -1 * days);
        return curDate;
    }

    public static String[] getWeekDateString() {
        String[] ret = new String[WEEK_COUNT];
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1 * WEEK_COUNT);
        for (int i=0; i<WEEK_COUNT; i++) {
            ret[i] = DateFormat.format("MM-dd", cal).toString();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return ret;
    }

    public static int countDaysBetween(long earlistEventTime, long latestEventTime) {
        long diff = latestEventTime - earlistEventTime;
        return (int) (diff / MS_ONE_DAY);
    }
}
