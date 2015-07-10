package com.maxiee.heartbeat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.GetAllCrashesApi;
import com.maxiee.heartbeat.model.Crash;
import com.maxiee.heartbeat.ui.adapter.CrashListAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-11.
 */
public class CrashListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Crash> crashes = new GetAllCrashesApi(this).exec();
        mRecyclerView.setAdapter(new CrashListAdapter(crashes));

        setTitle(getString(R.string.settings_crash));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
