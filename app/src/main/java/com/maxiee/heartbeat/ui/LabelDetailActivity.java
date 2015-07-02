package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.GetEventsByLabelKeyApi;
import com.maxiee.heartbeat.database.api.HasLabelApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-27.
 */
public class LabelDetailActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private String mLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_detail);

        Intent i = getIntent();

        mLabel = i.getStringExtra("tag_text");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        if (mLabel != null) {
            setTitle(mLabel);
            int labelKey = new HasLabelApi(this, mLabel).exec();
            updateEventList(labelKey);
        }
    }

    public void updateEventList(int labelKey) {
        ArrayList<Event> eventList = new GetEventsByLabelKeyApi(this, labelKey).exec();
        mRecyclerView.setAdapter(new EventListAdapter(eventList));
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
