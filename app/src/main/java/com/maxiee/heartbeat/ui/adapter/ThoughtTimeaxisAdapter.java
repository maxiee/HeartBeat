package com.maxiee.heartbeat.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.GalleryUtils;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.AddEditThoughtActivity;
import com.maxiee.heartbeat.ui.GalleryActivity;


/**
 * Created by maxiee on 15-6-13.
 */
public class ThoughtTimeaxisAdapter extends RecyclerView.Adapter<ThoughtTimeaxisAdapter.ViewHolder> {

    private Thoughts mThoughtList;

    public ThoughtTimeaxisAdapter(Thoughts thoughtList) {
        mThoughtList = thoughtList;
    }

    public void setData(Thoughts thoughts) {
        mThoughtList = thoughts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thought_timeaxis, parent, false);
        final TypedValue typedValue = new TypedValue();
        parent.getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int color = typedValue.data;
        Drawable d = ContextCompat.getDrawable(parent.getContext(), R.drawable.circle_timeaxis);
        d.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        View point = v.findViewById(R.id.time_point);
        point.setBackgroundDrawable(d);
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

        if (mThoughtList.get(position).hasImage()) {
            holder.mImage.setVisibility(View.VISIBLE);
            Glide.with(holder.mContext).load(
                    GalleryUtils.getImagePath(
                            mThoughtList.get(position).getPath())).into(holder.mImage);
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(holder.mContext, GalleryActivity.class);
                    i.putExtra(GalleryActivity.PATH, mThoughtList.get(position).getPath());
                    holder.mContext.startActivity(i);
                }
            });
        } else {
            holder.mImage.setVisibility(View.GONE);
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(holder.mContext, AddEditThoughtActivity.class);
                i.putExtra(AddEditThoughtActivity.MODE, AddEditThoughtActivity.MODE_EDIT);
                i.putExtra(AddEditThoughtActivity.THOUGHT_ID, mThoughtList.get(position).getKey());
                i.putExtra(AddEditThoughtActivity.THOUGHT, mThoughtList.get(position).getThought());
                i.putExtra(Thoughts.Thought.THOUGHT_RES, mThoughtList.get(position).getResType());
                i.putExtra(Thoughts.Thought.THOUGHT_PATH, mThoughtList.get(position).getPath());
                i.putExtra(AddEditThoughtActivity.TIMESTAMP, mThoughtList.get(position).getTimeStamp());
                holder.mContext.startActivity(i);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThoughtList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrder, tvThought, tvTime;
        public ImageView mImage;
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
            mImage = (ImageView) itemView.findViewById(R.id.image_thought);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            sortingType = sp.getString("time_axis_sorting", "0");
        }
    }
}
