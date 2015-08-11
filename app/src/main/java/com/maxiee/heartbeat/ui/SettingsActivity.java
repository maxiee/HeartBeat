package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.backup.BackupManager;
import com.maxiee.heartbeat.ui.fragments.SettingsFragment;

/**
 * Created by maxiee on 15-6-28.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();

        setTitle(getString(R.string.action_settings));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
