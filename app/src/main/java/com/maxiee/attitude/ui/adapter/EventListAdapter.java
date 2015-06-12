package com.maxiee.attitude.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maxiee.attitude.R;
import com.maxiee.attitude.model.Event;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private ArrayList<Event> mEventList;

    public EventListAdapter(ArrayList<Event> mEventList) {
        this.mEventList = mEventList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = mEventList.get(position);

        holder.tvEvent.setText(event.getmEvent());
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEvent, tvTime,tvThoughtCount;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvEvent = (TextView) itemView.findViewById(R.id.tv_event);
            this.tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            this.tvThoughtCount = (TextView) itemView.findViewById(R.id.tv_thought_count);
        }
    }
}
