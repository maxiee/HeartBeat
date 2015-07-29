package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by maxiee on 15-7-29.
 */
public class EntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("hb", Context.MODE_PRIVATE);
        String pattern = sp.getString("pattern", "");
        if (pattern.isEmpty()) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(this, PatternActivity.class);
            i.putExtra(PatternActivity.ACTION, PatternActivity.VERIFY);
            startActivity(i);
            finish();
        }
    }
}
