package com.maxiee.heartbeat.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.model.Crash;
import com.maxiee.heartbeat.ui.CrashDetailActivity;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-7-11.
 */
public class CrashListAdapter extends RecyclerView.Adapter<CrashListAdapter.ViewHolder> {

    private ArrayList<Crash> mCrashList;

    public CrashListAdapter(ArrayList<Crash> crashList) {
        this.mCrashList = crashList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crash_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Crash crash = mCrashList.get(position);

        holder.tvCrash.setText(crash.log);
        holder.tvTime.setText(
                TimeUtils.parseTime(
                        holder.mContext,
                        crash.timeStamp
                )
        );
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CrashDetailActivity.class);
                intent.putExtra(Crash.LOG, crash.log);
                intent.putExtra(Crash.TS, crash.timeStamp);
                ((Activity)context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCrashList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCrash, tvTime;
        public Context mContext;
        public final View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mView = itemView;
            tvCrash = (TextView) itemView.findViewById(R.id.tv_crash);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
