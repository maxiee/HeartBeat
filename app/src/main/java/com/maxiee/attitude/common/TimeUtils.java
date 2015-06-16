package com.maxiee.attitude.common;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.maxiee.attitude.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by maxiee on 15-6-14.
 */
public class TimeUtils {

    private static final int MS_ONE_SEC = 1000;
    private static final int SEC_ONE_MIN = 60 * MS_ONE_SEC;
    private static final int SEC_ONE_HOUR = SEC_ONE_MIN * 60;
    private static final int SEC_ONE_DAY = SEC_ONE_HOUR * 24;
    private static final int SEC_TEN_DAYS = SEC_ONE_DAY * 10;

    public static String parseTime(final Context context, final long timestamp) {
        Long timeNow = System.currentTimeMillis();
        Long delta = timeNow - timestamp;

        if (delta > SEC_TEN_DAYS) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            return DateFormat.format("yyyy-MM-dd", cal).toString();
        }

        long count = 0;
        String timeUnit = "";

        if (delta < SEC_ONE_HOUR) {
            count = delta/SEC_ONE_MIN;
            timeUnit = context.getString(R.string.minute_ago);
        } else if (delta < SEC_ONE_DAY) {
            count = delta/SEC_ONE_HOUR;
            timeUnit = context.getString(R.string.hour_ago);
        } else if (delta < SEC_TEN_DAYS) {
            count = delta/SEC_ONE_DAY;
            timeUnit = context.getString(R.string.day_ago);
        }
        return String.valueOf(count) + timeUnit;
    }
}
