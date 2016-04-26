package com.maxiee.heartbeat.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.widget.RemoteViews;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.ThemeUtils;
import com.maxiee.heartbeat.ui.AddEventActivity;
import com.maxiee.heartbeat.ui.EntryActivity;

/**
 * Created by maxiee on 16/4/26.
 */
public class HeartBeatWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int n = appWidgetIds.length;

        int currentTheme = ThemeUtils.getCurrentActivityTheme(context);
        context.setTheme(currentTheme);

        int attrs[] = {R.attr.colorAccent, R.attr.colorPrimary, R.attr.colorPrimaryDark};
        TypedArray ta = context.obtainStyledAttributes(currentTheme, attrs);

        int colorAccent = ThemeUtils.getAttributeColor(context, R.attr.colorAccent);
        int colorPrimary = ThemeUtils.getAttributeColor(context, R.attr.colorPrimary);
        int colorPrimaryDark = ThemeUtils.getAttributeColor(context, R.attr.colorPrimaryDark);

        ta.recycle();

        for (int i=0; i<n; i++) {
            Intent intentOpenApp = new Intent(context, EntryActivity.class);
            PendingIntent pendingIntentOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, 0);

            Intent intentAddEvent = new Intent(context, AddEventActivity.class);
            PendingIntent pendingIntentAddEvent = PendingIntent.getActivity(context, 0, intentAddEvent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.open_app, pendingIntentOpenApp);
            views.setOnClickPendingIntent(R.id.add_event, pendingIntentAddEvent);
            views.setTextColor(R.id.open_app, colorAccent);
            views.setTextColor(R.id.add_event, colorAccent);

            views.setTextColor(R.id.event_text, colorPrimaryDark);
            views.setTextColor(R.id.event_count, colorAccent);
            views.setTextColor(R.id.thought_text, colorPrimaryDark);
            views.setTextColor(R.id.thought_count, colorAccent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }
}
