package com.maxiee.attitude.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maxiee.attitude.R;
import com.maxiee.attitude.database.api.BaseDBApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by maxiee on 15-6-13.
 */
public class ThoughtTimeaxisAdapter extends RecyclerView.Adapter<ThoughtTimeaxisAdapter.ViewHolder> {

    private JSONArray mThoughtList;

    public ThoughtTimeaxisAdapter(JSONArray thoughtList) {
        mThoughtList = thoughtList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thought_timeaxis, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String order;
        switch (position) {
            case 0:
                order = holder.mContext.getString(R.string.firtime);
                break;
            case 1:
                order = holder.mContext.getString(R.string.sectime);
                break;
            default:
                order = String.valueOf(position) + ".";
                break;
        }
        holder.tvOrder.setText(order);

        try {
            JSONObject thoughtObject = (JSONObject) mThoughtList.get(position);
            String thought = thoughtObject.getString(BaseDBApi.THOUGHT);
            String time = String.valueOf(thoughtObject.get(BaseDBApi.TIMESTAMP));
            holder.tvThought.setText(thought);
            holder.tvTime.setText(time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mThoughtList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrder, tvThought, tvTime;
        public Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            tvOrder = (TextView) itemView.findViewById(R.id.tv_order);
            tvThought = (TextView) itemView.findViewById(R.id.tv_thought);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
