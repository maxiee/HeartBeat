package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
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
    private TextView mLabelHint;
    private String mLabel;
    private ArrayList<Event> mEventList;
    private int mAccentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_detail);

        Intent i = getIntent();

        mLabel = i.getStringExtra("tag_text");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLabelHint = (TextView) findViewById(R.id.label_hint);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        TypedValue accentValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, accentValue, true);
        mAccentColor = accentValue.data;

        if (mLabel != null) {
            int labelKey = new HasLabelApi(this, mLabel).exec();
            updateEventList(labelKey);
            showHintText();
        }
    }

    public void updateEventList(int labelKey) {
        mEventList = new GetEventsByLabelKeyApi(this, labelKey).exec();
        mRecyclerView.setAdapter(new EventListAdapter(mEventList));
    }

    public void showHintText() {
        if (mEventList.size() < 1) return;
        long earlistEventTime = mEventList.get(mEventList.size() - 1).getTimestamp();
        long latestEventTime = System.currentTimeMillis();
        ArrayList<String> hint = new ArrayList<>();
        hint.add(mLabel + "\n");
        hint.add(getString(R.string.label_hint_001));
        hint.add(TimeUtils.parseTime(this, earlistEventTime));
        hint.add(getString(R.string.label_hint_002));
        hint.add(String.valueOf(mEventList.size()));
        hint.add(getString(R.string.label_hint_003));
        hint.add(String.valueOf(
                TimeUtils.countDaysBetween(
                        earlistEventTime,
                        latestEventTime)));
        hint.add(getString(R.string.label_hint_004));
        String textToSpan = "";
        for (int i=0; i<hint.size(); i++) {
            textToSpan += hint.get(i);
        }
        Spannable spannable = new SpannableString(textToSpan);
        int offset = 0;
        for (int i=0; i<hint.size(); i++) {
            if (i == 0) {
                spannable.setSpan(
                        new RelativeSizeSpan(1.5f),
                        0,
                        mLabel.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannable.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        0,
                        mLabel.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            ForegroundColorSpan span;
            if (i==0 || i==2 || i==4 || i==6) {
                span = new ForegroundColorSpan(mAccentColor);
            } else {
                span = new ForegroundColorSpan(getResources().getColor(R.color.window_background));
            }
            int newOffset = offset + hint.get(i).length();
            spannable.setSpan(
                    span,
                    offset,
                    newOffset,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            offset = newOffset;
        }
        mLabelHint.setText(spannable, TextView.BufferType.SPANNABLE);
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
