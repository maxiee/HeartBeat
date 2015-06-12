package com.maxiee.attitude.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxiee.attitude.R;
import com.maxiee.attitude.database.api.GetAllEventApi;
import com.maxiee.attitude.model.Event;
import com.maxiee.attitude.ui.adapter.EventListAdapter;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class EventListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_event_list, container, false);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        try {
            ArrayList<Event> eventList = new GetAllEventApi(getActivity()).exec();
            if (eventList != null) {
                rv.setAdapter(new EventListAdapter(eventList));
            }
        } catch (JSONException e) {e.printStackTrace();}
        return rv;
    }
}
