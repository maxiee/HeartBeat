package com.maxiee.heartbeat.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.GetAllEventApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class EventListFragment extends Fragment {
    private static final String TAG = EventListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RelativeLayout mEmptyLayout;
    private ImageView mImageEmpty;
    private ArrayList<Event> mEventList;
    private EventListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.fragment_event_list, container, false);
        mEmptyLayout = (RelativeLayout) v.findViewById(R.id.empty);
        mImageEmpty = (ImageView) v.findViewById(R.id.image_empty);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mEventList = new GetAllEventApi(getActivity()).exec();
        mAdapter = new EventListAdapter(mEventList);
        updateEventList();
        setHasOptionsMenu(true);
        return v;
    }

    public void updateEventList() {
        if (!mEventList.isEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(R.drawable.empty_bg).into(mImageEmpty);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Event> newEvents = new GetAllEventApi(getContext()).exec();
        if (!newEvents.isEmpty()) {
            mEventList.addAll(0, newEvents);
            mAdapter.notifyDataSetChanged();
        }
    }
}
