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

/**
 * Created by maxiee on 15-6-23.
 */
public class EventTodayFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private RelativeLayout mEmtpyLayout;
    private ImageView mImageEmpty;
    private DataManager mDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_today, container, false);

        mEmtpyLayout = (RelativeLayout) v.findViewById(R.id.empty);
        mImageEmpty = (ImageView) v.findViewById(R.id.image_empty);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));

        mDataManager = DataManager.getInstance(getContext());
        updateEventList();

        return v;
    }

    public void updateEventList() {
        if (!mDataManager.isTodayEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmtpyLayout.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mDataManager.getTodayAdapter());
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmtpyLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(R.drawable.empty_bg2).into(mImageEmpty);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataManager.notifyDataSetChanged();
        updateEventList();
    }

}
