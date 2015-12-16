package com.maxiee.heartbeat.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maxiee.heartbeat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15/12/15.
 */
public class HelpListAdapter extends RecyclerView.Adapter<HelpListAdapter.ViewHolder> {

    private String[] mTitles;
    private String[] mDescriptions;
    private String[] mFilenames;

    public HelpListAdapter(String[] titles, String[] descriptions, String[] filenames) {
        mTitles = titles;
        mDescriptions = descriptions;
        mFilenames = filenames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_help_center, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mTitles[position]);
        holder.description.setText(mDescriptions[position]);
    }

    @Override
    public int getItemCount() {
        return mTitles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)       TextView title;
        @Bind(R.id.description) TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
