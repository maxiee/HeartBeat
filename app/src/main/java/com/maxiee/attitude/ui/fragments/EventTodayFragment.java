package com.maxiee.attitude.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxiee.attitude.R;
import com.maxiee.attitude.database.api.GetTodayEventApi;
import com.maxiee.attitude.model.Event;
import com.maxiee.attitude.ui.adapter.EventListAdapter;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-23.
 */
public class EventTodayFragment extends Fragment{

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_today, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        updateEventList();

        return v;
    }

    public void updateEventList() {
        ArrayList<Event>  eventList = new GetTodayEventApi(getActivity()).exec();
        if (eventList != null) {
            mRecyclerView.setAdapter(new EventListAdapter(eventList));
        }
    }
}
