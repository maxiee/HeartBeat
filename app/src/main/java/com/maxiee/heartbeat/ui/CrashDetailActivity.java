package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.model.Crash;

/**
 * Created by maxiee on 15-7-11.
 */
public class CrashDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTvCrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvCrash = (TextView) findViewById(R.id.tv_crash);

        Intent intent = getIntent();
        String crash = intent.getStringExtra(Crash.LOG);
        mTvCrash.setText(crash);

        setTitle(getString(R.string.settings_crash));
    }
}
