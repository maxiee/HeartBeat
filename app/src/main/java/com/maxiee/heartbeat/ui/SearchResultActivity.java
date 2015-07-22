package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.EventSearchApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-21.
 */
public class SearchResultActivity extends AppCompatActivity{
    private static final String TAG = SearchResultActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        Intent i = getIntent();
        String search = i.getStringExtra("search");

        if (!search.isEmpty()) {
            setTitle(search);
            updateEventList(search);
        }

        Log.d(TAG, search);
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

    public void updateEventList(String search) {
        ArrayList<Event> eventList = new EventSearchApi(this, search).exec();
        if (eventList != null) {
            mRecyclerView.setAdapter(new EventListAdapter(eventList));
        }
    }
}
