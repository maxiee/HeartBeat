package com.maxiee.heartbeat.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.DayCard;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Image;
import com.maxiee.heartbeat.ui.AddEventActivity;
import com.maxiee.heartbeat.ui.EventDetailActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 16/1/22.
 */
public class DayCardEventAdapter extends RecyclerView.Adapter<DayCardEventAdapter.DayCardHolder>{

    private ArrayList<DayCard> mDayCardData;

    public DayCardEventAdapter(ArrayList<DayCard> dayCardData) {
        mDayCardData = dayCardData;
    }

    public void setData(ArrayList<DayCard>  dayCardData) {
        mDayCardData = dayCardData;
    }

    @Override
    public DayCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_card, parent, false);
        return new DayCardHolder(v);
    }

    @Override
    public void onBindViewHolder(DayCardHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mDayCardData.size();
    }


    class DayCardHolder extends RecyclerView.ViewHolder {
        Context context;
        @Bind(R.id.day_card_head_date) TextView headerDate;
        @Bind(R.id.item_layout) LinearLayout itemLayout;

        DayCardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        void onBind(int position) {
            DayCard dayCard = mDayCardData.get(position);
            headerDate.setText(
                    TimeUtils.parseDateDate(context, dayCard.getTimeStamp()));
            itemLayout.removeAllViewsInLayout();
            for (Event e: dayCard.getEventList()) {
                View v = LayoutInflater.from(context)
                        .inflate(R.layout.item_day_card_item, itemLayout, false);
                final Event event = e;
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EventDetailActivity.class);
                        intent.putExtra(
                                EventDetailActivity.EXTRA_NAME,
                                event.getId());
                        context.startActivity(intent);
                    }
                });

                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        final String[] items = new String[]{
                                v.getContext().getString(R.string.dialog_edit_event),
                                v.getContext().getString(R.string.delete)
                        };
                        final Context context = v.getContext();
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent i = new Intent(context, AddEventActivity.class);
                                    i.putExtra(AddEventActivity.ID_EVENT_MODIFY, event.getId());
                                    context.startActivity(i);
                                }
                                if (which == 1) {
                                    DataManager.getInstance(context).deleteEvent(event.getId());
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                });
                TextView eventText = ButterKnife.findById(v, R.id.event_text);
                TextView timeHour = ButterKnife.findById(v, R.id.time_hour);
                TextView timeMinute = ButterKnife.findById(v, R.id.time_minute);
                TextView thoughtCount = ButterKnife.findById(v, R.id.tv_thought_count);
                ImageView eventImage = ButterKnife.findById(v, R.id.event_image);
                FrameLayout imageContainer = ButterKnife.findById(v, R.id.image_container);
                View separator = ButterKnife.findById(v, R.id.separator);

                eventText.setText(e.getEvent());
                timeHour.setText(TimeUtils.parseHour(context, e.getTimestamp()));
                timeMinute.setText(TimeUtils.parseMinute(context, e.getTimestamp()));
                thoughtCount.setText(String.valueOf(ThoughtUtils.getEventCount(context, e.getId())));
                Image i = ImageUtils.getImageByEventId(context, e.getId());
                if (i != null) {
                    imageContainer.setVisibility(View.VISIBLE);
                    Glide.with(context).load(i.getPath()).centerCrop().into(eventImage);
                } else eventImage.setVisibility(View.GONE);
                if (e.equals(dayCard.getEventList().get(dayCard.getEventList().size() - 1)))
                    separator.setVisibility(View.GONE);

                itemLayout.addView(v);
            }
        }
    }
}
