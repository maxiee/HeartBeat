package com.maxiee.heartbeat.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import com.google.android.agera.rvadapter.RepositoryPresenter;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.GalleryUtils;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 16/7/2.
 */
public class DayCardPresenter extends RepositoryPresenter<List<DayCard>> {
    @Override
    public int getItemCount(@NonNull List<DayCard> data) {
        return data.size();
    }

    @Override
    public int getLayoutResId(@NonNull List<DayCard> data, int index) {
        return R.layout.item_day_card;
    }

    @Override
    public void bind(@NonNull List<DayCard> data, int index, @NonNull RecyclerView.ViewHolder viewHolder) {
        final DayCard dayCard = data.get(index);
        final DayCardHolder holder = new DayCardHolder(viewHolder.itemView, dayCard);
        holder.onBind();
    }

    public class DayCardHolder extends RecyclerView.ViewHolder {
        Context context;
        @Bind(R.id.day_card_head_date)
        TextView headerDate;
        @Bind(R.id.item_layout)
        LinearLayout itemLayout;
        DayCard dayCard;
        private ArrayList<AsyncTask> mTasks = new ArrayList<>();

        DayCardHolder(View itemView, DayCard dayCard) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            this.dayCard = dayCard;
        }

        void onBind() {
            headerDate.setText(
                    TimeUtils.parseDateDate(context, dayCard.getTimeStamp()));

            float itemHeight = context.getResources().getDimension(R.dimen.day_card_item_height);
            int eventCount = dayCard.getEventList().size();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) itemHeight * eventCount);
            itemLayout.setLayoutParams(lp);
            itemLayout.removeAllViewsInLayout();
            for (Event e: dayCard.getEventList()) {
                boolean drawSeparator = e.equals(dayCard.getEventList().get(dayCard.getEventList().size() - 1));
                DayCardItemTask task = new DayCardItemTask(e, drawSeparator);
                mTasks.add(task);
                task.execute();
            }
        }

        void onViewRecycled() {
            for (AsyncTask t: mTasks) t.cancel(true);
        }

        class DayCardItemTask extends AsyncTask<Void, Void, View> {

            private Event mEvent;
            private boolean mDrawSeparator;

            public  DayCardItemTask (Event event, boolean drawSeparator) {
                mEvent = event;
                mDrawSeparator = drawSeparator;
            }


            @Override
            protected View doInBackground(Void... params) {
                return LayoutInflater.from(context)
                        .inflate(R.layout.item_day_card_item, itemLayout, false);
            }

            @Override
            protected void onPostExecute(View v) {
                v.setOnClickListener(v1 -> {
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(
                            EventDetailActivity.EXTRA_NAME,
                            mEvent.getId());
                    context.startActivity(intent);
                });

                v.setOnLongClickListener(v1 -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v1.getContext());
                    final String[] items = new String[]{
                            v1.getContext().getString(R.string.dialog_edit_event),
                            v1.getContext().getString(R.string.delete)
                    };
                    final Context context1 = v1.getContext();
                    builder.setItems(items, (dialog, which) -> {
                        if (which == 0) {
                            Intent i = new Intent(context1, AddEventActivity.class);
                            i.putExtra(AddEventActivity.ID_EVENT_MODIFY, mEvent.getId());
                            context1.startActivity(i);
                        }
                        if (which == 1) {
                            DataManager.getInstance(context1).deleteEvent(mEvent.getId());
                        }
                    });
                    builder.show();
                    return true;
                });
                TextView eventText = ButterKnife.findById(v, R.id.event_text);
                TextView thoughtCount = ButterKnife.findById(v, R.id.tv_thought_count);
                ImageView eventImage = ButterKnife.findById(v, R.id.event_image);
                FrameLayout imageContainer = ButterKnife.findById(v, R.id.image_container);
                View separator = ButterKnife.findById(v, R.id.separator);

                eventText.setText(mEvent.getEvent());
                thoughtCount.setText(String.valueOf(ThoughtUtils.getEventCount(context, mEvent.getId())));
                Image i = ImageUtils.getImageByEventId(context, mEvent.getId());
                if (i != null) {
                    imageContainer.setVisibility(View.VISIBLE);
                    Glide.with(context).load(
                            GalleryUtils.getImagePath(
                                    i.getPath())).centerCrop().into(eventImage);
                } else eventImage.setVisibility(View.GONE);
                if (mDrawSeparator) separator.setVisibility(View.GONE);
                itemLayout.addView(v);
            }
        }
    }
}
