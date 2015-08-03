package com.maxiee.heartbeat.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.dialog.EditThoughtDialog;


/**
 * Created by maxiee on 15-6-13.
 */
public class ThoughtTimeaxisAdapter extends RecyclerView.Adapter<ThoughtTimeaxisAdapter.ViewHolder> {

    private Thoughts mThoughtList;

    public ThoughtTimeaxisAdapter(Thoughts thoughtList) {
        mThoughtList = thoughtList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thought_timeaxis, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String order;
        if (holder.sortingType.equals("0")) {
            switch (position) {
                case 0:
                    order = holder.mContext.getString(R.string.firtime);
                    break;
                case 1:
                    order = holder.mContext.getString(R.string.sectime);
                    break;
                default:
                    order = String.valueOf(position + 1) + ".";
                    break;
            }
        } else {
            if (position == mThoughtList.length() - 1) {
                order = holder.mContext.getString(R.string.firtime);
            } else if (position == mThoughtList.length() - 2) {
                order = holder.mContext.getString(R.string.sectime);
            } else {
                order = String.valueOf(mThoughtList.length() - position) + ".";
            }
        }
        holder.tvOrder.setText(order);

        long time = mThoughtList.get(position).getTimeStamp();
        holder.tvThought.setText(mThoughtList.get(position).getThought());
        holder.tvTime.setText(TimeUtils.parseTime(holder.mContext, time));

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditThoughtDialog dialog = new EditThoughtDialog(
                        holder.mContext,
                        mThoughtList.get(position).getKey(),
                        mThoughtList.get(position).getThought()
                );
                dialog.setOnAddFinishedListener(new EditThoughtDialog.OnAddFinishedListener() {
                    @Override
                    public void update(String newThought) {
                        mThoughtList.get(position).setThought(newThought);
                        holder.tvThought.setText(newThought);
                    }

                    @Override
                    public void remove() {
                        mThoughtList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                dialog.show();
                return true;
            }
        });
        Log.d("增删测试",
                String.valueOf(mThoughtList.get(position).getKey()) + ": " +
                mThoughtList.get(position).getThought()
        );
    }

    @Override
    public int getItemCount() {
        return mThoughtList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrder, tvThought, tvTime;
        public final View mView;
        public Context mContext;
        public String sortingType;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mContext = itemView.getContext();
            tvOrder = (TextView) itemView.findViewById(R.id.tv_order);
            tvThought = (TextView) itemView.findViewById(R.id.tv_thought);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            sortingType = sp.getString("time_axis_sorting", "0");
        }
    }
}
