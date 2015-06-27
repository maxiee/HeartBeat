package com.maxiee.attitude.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.maxiee.attitude.R;

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

        if (mLabel == null) {
            mLabel = getString(R.string.app_name);
        }

        setTitle(mLabel);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

    }
}
