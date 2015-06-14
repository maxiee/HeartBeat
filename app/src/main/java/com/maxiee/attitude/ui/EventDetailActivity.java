package com.maxiee.attitude.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.maxiee.attitude.R;
import com.maxiee.attitude.database.api.GetOneEventApi;
import com.maxiee.attitude.model.Event;
import com.maxiee.attitude.ui.adapter.ThoughtTimeaxisAdapter;
import com.maxiee.attitude.ui.dialog.NewThoughtDialog;

import org.json.JSONException;

/**
 * Created by maxiee on 15-6-13.
 */
public class EventDetailActivity extends AppCompatActivity {

    private Event mEvent;

    private TextView mTvEvent;
    private RecyclerView mRecyclerView;
    private ThoughtTimeaxisAdapter mAdapter;
    private int mId;

    public static final String EXTRA_NAME = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        mId = intent.getIntExtra(EXTRA_NAME, -1);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvEvent = (TextView) findViewById(R.id.tv_event);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        try {
            mEvent =  new GetOneEventApi(this, mId).exec();
            mTvEvent.setText(mEvent.getmEvent());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new ThoughtTimeaxisAdapter(mEvent.getmThoughts());
            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewThoughtDialog dialog = new NewThoughtDialog(EventDetailActivity.this, mId);
                dialog.setOnAddFinishedListener(new NewThoughtDialog.OnAddFinishedListener() {
                    @Override
                    public void update() {
                        try {
                            mEvent = new GetOneEventApi(EventDetailActivity.this, mId).exec();
                            mAdapter = new ThoughtTimeaxisAdapter(mEvent.getmThoughts());
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        });
    }
}
