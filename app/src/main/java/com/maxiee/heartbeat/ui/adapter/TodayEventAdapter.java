package com.maxiee.heartbeat.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Image;
import com.maxiee.heartbeat.ui.EventDetailActivity;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-12.
 */
public class TodayEventAdapter extends RecyclerView.Adapter<TodayEventAdapter.ViewHolder> {

    private static final String TAG = TodayEventAdapter.class.getSimpleName();

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private ArrayList<Event> mEventList;

    public TodayEventAdapter(ArrayList<Event> mEventList) {
        this.mEventList = mEventList;
    }

    public void setData(ArrayList<Event> mEventList) {
        this.mEventList = mEventList;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_today_header, parent, false);
            return new ViewHolder(v, ITEM_VIEW_TYPE_HEADER);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event_card, parent, false);
            return new ViewHolder(v, ITEM_VIEW_TYPE_ITEM);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder.mViewType == ITEM_VIEW_TYPE_ITEM) {
            final Event event = mEventList.get(position - 1);

            holder.tvEvent.setText(event.getEvent());
            holder.tvTime.setText(
                    TimeUtils.parseTime(
                            holder.mView.getContext(),
                            event.getTimestamp()));

            holder.tvThoughtCount.setText(
                    String.valueOf(ThoughtUtils.getEventCount(holder.mContext, event.getId())));

//        Log.d(TAG, "事件列表项目");
//        Log.d(TAG, "编号:" + String.valueOf(event.getId()));
//        Log.d(TAG, "名称:" + event.getEvent());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(
                            EventDetailActivity.EXTRA_NAME,
                            mEventList.get(position - 1).getId());
                    context.startActivity(intent);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    final String[] items = new String[]{
                            v.getContext().getString(R.string.delete)
                    };
                    final Context context = v.getContext();
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                DataManager.getInstance(context).deleteEvent(event.getId());
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });

            Image i = ImageUtils.getImageByEventId(holder.mContext, event.getId());

            int marginTBHasImage = (int) holder.mContext.getResources().getDimension(R.dimen.item_event_has_image_margin_tb);
            int marginTBNoImage = (int) holder.mContext.getResources().getDimension(R.dimen.item_event_no_image_margin_tb);
            int marginLR = (int) holder.mContext.getResources().getDimension(R.dimen.item_event_image_margin_lr);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.tvEvent.getLayoutParams();

            if (i != null) {
                holder.mCoverImage.setVisibility(View.VISIBLE);
                Glide.with(holder.mContext)
                        .load(i.getPath())
                        .centerCrop()
                        .into(holder.mCoverImage);
                params.setMargins(marginLR, marginTBHasImage, marginLR, marginTBHasImage);
            } else {
                holder.mCoverImage.setVisibility(View.GONE);
                params.setMargins(marginLR, marginTBNoImage, marginLR, marginTBNoImage);
            }
        } else {
            DataManager dm = DataManager.getInstance(holder.mContext);
            holder.tvEventCount.setText(String.valueOf(dm.getTodayEventCount()));
            holder.tvThoughtCount.setText(String.valueOf(dm.getTodayThoughtCount()));
            showTodayHint(dm.getTodayThoughtCount(), holder.tvTodayHint, holder.mContext);
        }
    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mEventList.size() + 1;
    }

    private static void showTodayHint(int thoughtCount, TextView tvHint, Context context) {
            String[] hints = context.getResources().getStringArray(R.array.today_tips);
            if (thoughtCount < 2) {
                tvHint.setText(hints[0]);
            } else if (thoughtCount < 5) {
                tvHint.setText(hints[1]);
            } else if (thoughtCount < 10) {
                tvHint.setText(hints[2]);
            } else {
                tvHint.setText(hints[3]);
            }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEvent, tvTime,tvThoughtCount;
        public TextView tvEventCount, tvTodayHint;
        public final View mView;
        public Context mContext;
        public ImageView mCoverImage;
        public int mViewType;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            mViewType = viewType;
            mContext = itemView.getContext();
            if (viewType == ITEM_VIEW_TYPE_ITEM) {
                this.mView = itemView;
                this.tvEvent = (TextView) itemView.findViewById(R.id.tv_event);
                this.tvTime = (TextView) itemView.findViewById(R.id.tv_time);
                this.tvThoughtCount = (TextView) itemView.findViewById(R.id.tv_thought_count);
                this.mCoverImage = (ImageView) itemView.findViewById(R.id.image_cover);
            } else {
                mView = itemView;
                tvEventCount = (TextView) itemView.findViewById(R.id.tv_event_count);
                tvThoughtCount = (TextView) itemView.findViewById(R.id.tv_thought_count);
                tvTodayHint = (TextView) itemView.findViewById(R.id.tv_today_hint);
            }
        }
    }
}
