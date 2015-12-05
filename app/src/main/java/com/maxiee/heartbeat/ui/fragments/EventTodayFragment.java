package com.maxiee.heartbeat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.ui.common.RecyclerInsetsDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15-6-23.
 */
public class EventTodayFragment extends Fragment{

    @Bind(R.id.recyclerview)    RecyclerView mRecyclerView;
    @Bind(R.id.empty)           RelativeLayout mEmtpyLayout;
    @Bind(R.id.image_empty)     ImageView mImageEmpty;
    private DataManager mDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_today, container, false);
        ButterKnife.bind(this, v);

        mDataManager = DataManager.getInstance(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(getContext()));
        mRecyclerView.setAdapter(mDataManager.getTodayAdapter());
        updateEventList();

        return v;
    }

    public void updateEventList() {
        if (!mDataManager.isTodayEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmtpyLayout.setVisibility(View.GONE);
            mDataManager.notifyDataSetChanged();
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmtpyLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(R.drawable.empty_bg2).into(mImageEmpty);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataManager.checkNewDay();
        updateEventList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
