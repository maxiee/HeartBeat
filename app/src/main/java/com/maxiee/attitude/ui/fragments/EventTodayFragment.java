package com.maxiee.attitude.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxiee.attitude.R;
import com.maxiee.attitude.ui.adapter.EventListAdapter;

/**
 * Created by maxiee on 15-6-23.
 */
public class EventTodayFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_today, container, false);
        return v;
    }
}
