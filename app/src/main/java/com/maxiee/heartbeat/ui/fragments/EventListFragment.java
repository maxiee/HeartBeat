package com.maxiee.heartbeat.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.data.DataManager;

/**
 * Created by maxiee on 15-6-12.
 */
public class EventListFragment extends Fragment {
    private static final String TAG = EventListFragment.class.getSimpleName();

    private static final String SP_VIEW_MODE = "event_list_view_mode";
    private static final int VIEW_MODE_LIST = 0;
    private static final int VIEW_MODE_STAGGERED = 1;

    private RecyclerView mRecyclerView;
    private RelativeLayout mEmptyLayout;
    private ImageView mImageEmpty;
    private int mViewMode;
    private MenuItem mViewModeMenu;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private SharedPreferences mPrefs;
    private DataManager mDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.fragment_event_list, container, false);
        mEmptyLayout = (RelativeLayout) v.findViewById(R.id.empty);
        mImageEmpty = (ImageView) v.findViewById(R.id.image_empty);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        mViewMode = mPrefs.getInt(SP_VIEW_MODE, VIEW_MODE_STAGGERED);
        if (mViewMode == VIEW_MODE_STAGGERED) mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        if (mViewMode == VIEW_MODE_LIST) mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mDataManager = DataManager.getInstance(getContext());
        updateEventList();
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_eventlist_type, menu);
        mViewModeMenu = menu.findItem(R.id.type);
        mViewModeMenu.setIcon(getViewModeIconRes());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.type) {
            int currentViewMode = mViewMode;
            if (currentViewMode == VIEW_MODE_LIST) {
                mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                mViewMode = VIEW_MODE_STAGGERED;
            }
            if (currentViewMode == VIEW_MODE_STAGGERED) {
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                mViewMode = VIEW_MODE_LIST;
            }
            mViewModeMenu.setIcon(getViewModeIconRes());
            mPrefs.edit().putInt(SP_VIEW_MODE, mViewMode).apply();
            return true;
        }
        return false;
    }

    public void updateEventList() {
        if (!mDataManager.isEventEmpty()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mDataManager.getEventAdapter());
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(R.drawable.empty_bg).into(mImageEmpty);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataManager.notifyDataSetChanged();
        if (mViewMode == VIEW_MODE_STAGGERED) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mStaggeredGridLayoutManager.invalidateSpanAssignments();
                }
            });
        }
    }

    private int getViewModeIconRes() {
        if (mViewMode == VIEW_MODE_LIST) return R.drawable.ic_action_dashboard;
        else if (mViewMode == VIEW_MODE_STAGGERED) return R.drawable.ic_action_list;
        else {
            mViewMode = VIEW_MODE_STAGGERED;
            return R.drawable.ic_action_list;
        }
    }
}
