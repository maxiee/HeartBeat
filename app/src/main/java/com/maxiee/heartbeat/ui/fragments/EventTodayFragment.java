package com.maxiee.heartbeat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.GetThoughtTodayCountApi;
import com.maxiee.heartbeat.database.api.GetTodayEventApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-23.
 */
public class EventTodayFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private TextView mTvEventCount;
    private TextView mTvThoughtCount;
    private TextView mTvTodayHint;
    private LinearLayout mMainLayout;
    private RelativeLayout mEmtpyLayout;
    private ImageView mImageEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_today, container, false);

        mTvEventCount = (TextView) v.findViewById(R.id.tv_event_count);
        mTvThoughtCount = (TextView) v.findViewById(R.id.tv_thought_count);
        mTvTodayHint = (TextView) v.findViewById(R.id.tv_today_hint);
        mMainLayout = (LinearLayout) v.findViewById(R.id.main_layout);
        mEmtpyLayout = (RelativeLayout) v.findViewById(R.id.empty);
        mImageEmpty = (ImageView) v.findViewById(R.id.image_empty);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        updateEventList();

        return v;
    }

    public void updateEventList() {
        ArrayList<Event>  eventList = new GetTodayEventApi(getActivity()).exec();
        if (eventList != null) {
            mMainLayout.setVisibility(View.VISIBLE);
            mEmtpyLayout.setVisibility(View.GONE);
            mRecyclerView.setAdapter(new EventListAdapter(eventList));
            mTvEventCount.setText(String.valueOf(eventList.size()));
            int thoughtCount = new GetThoughtTodayCountApi(getActivity()).exec();
            if (thoughtCount != -1) {
                mTvThoughtCount.setText(String.valueOf(thoughtCount));
                showTodayHint(thoughtCount);
            }
        } else {
            mMainLayout.setVisibility(View.GONE);
            mEmtpyLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(R.drawable.empty_bg2).into(mImageEmpty);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEventList();
    }

    private void showTodayHint(int thoughtCount) {
        if (getActivity() != null) {
            String[] hints = getActivity().getResources().getStringArray(R.array.today_tips);
            if (thoughtCount < 2) {
                mTvTodayHint.setText(hints[0]);
            } else if (thoughtCount < 5) {
                mTvTodayHint.setText(hints[1]);
            } else if (thoughtCount < 10) {
                mTvTodayHint.setText(hints[2]);
            } else {
                mTvTodayHint.setText(hints[3]);
            }
        }
    }
}
