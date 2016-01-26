package com.maxiee.heartbeat.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFullFormat = sp.getBoolean("time_full_format", false);

        Long timeNow = System.currentTimeMillis();
        Long delta = timeNow - timestamp;

        if (isFullFormat || delta > MS_FOUR_DAYS) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            return DateFormat.format("yyyy-MM-dd HH:mm", cal).toString();
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

    public static String parseDateDate(final Context context, final long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("yyyy-MM-dd", cal).toString();
    }

    public static String parseDateTime(final Context context, final long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("HH:mm:ss", cal).toString();
    }

    public static String parseHour(final Context context, final long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("HH", cal).toString();
    }

    public static String parseMinute(final Context context, final long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("mm", cal).toString();
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

    public static boolean isInSameDay(long dayStart, long eventTime) {
        return eventTime > dayStart;
    }

    public static int getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static long getDayStart(long timestamp) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);
        date.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH),
                0, 0);
        return date.getTimeInMillis();
    }

    public static long getTodayMillis() {
        Calendar date = Calendar.getInstance();
        date.set(
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH),
            0, 0);
        return date.getTimeInMillis();
    }

    public static long updateTimestampWithDate(int year, int monthOfYear, int dayOfMonth, long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(year, monthOfYear, dayOfMonth);
        return cal.getTimeInMillis();
    }

    public static long updateTimestampWithTime(int hourOfDay, int minute, int second, long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        return cal.getTimeInMillis();
    }
}
