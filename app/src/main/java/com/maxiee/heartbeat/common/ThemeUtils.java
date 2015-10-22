package com.maxiee.heartbeat.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.ui.common.BaseActivity;

/**
 * Created by maxiee on 15/10/22.
 */
public class ThemeUtils {
    private static final String CURRENT_THEME = "current_theme";

    private static final int[] THEME_TITLES_RES = new int[] {
            R.string.theme_heartbeat,
            R.string.theme_test
    };

    private static final int[] THEME_ACTIVITY_RES = new int[] {
            R.style.AppTheme,
            R.style.AppTheme_test
    };

    public static void chooseThemeDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.change_theme));
        builder.setItems(getThemeTitles(context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCurrentActivityTheme(context, which);
                ((BaseActivity) context).recreate();
            }
        });
        builder.show();
    }

    private static CharSequence[] getThemeTitles(Context context) {
        CharSequence[] titles = new String[THEME_TITLES_RES.length];
        for (int i=0; i<THEME_TITLES_RES.length; i++) {
            titles[i] = context.getString(THEME_TITLES_RES[i]);
        }
        return titles;
    }

    public static int getCurrentActivityTheme(Context context) {
        SharedPreferences sp = getSharedPreference(context);
        int index = sp.getInt(CURRENT_THEME, 0);
        return THEME_ACTIVITY_RES[index];
    }

    private static void setCurrentActivityTheme(Context context, int index) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(CURRENT_THEME, index);
        editor.apply();
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences("hb", Context.MODE_PRIVATE);
    }

    public static int getAttributeColor(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        int color = 0x000000;
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // resId is a color
            color = typedValue.data;
        } else {
            // resId is not a color
        }
        return color;
    }
}
