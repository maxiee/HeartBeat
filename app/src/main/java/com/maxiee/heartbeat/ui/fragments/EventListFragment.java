package com.maxiee.heartbeat.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.maxiee.heartbeat.ui.common.RecyclerInsetsDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15-6-12.
 */
public class EventListFragment extends Fragment {
    private static final String TAG = EventListFragment.class.getSimpleName();

    private static final String SP_VIEW_MODE = "event_list_view_mode";
    private static final int VIEW_MODE_LIST = 0;
    private static final int VIEW_MODE_STAGGERED = 1;

    @Bind(R.id.refresher)       SwipeRefreshLayout  mRefresher;
    @Bind(R.id.recyclerview)    RecyclerView        mRecyclerView;
    @Bind(R.id.empty)           RelativeLayout      mEmptyLayout;
    @Bind(R.id.image_empty)     ImageView           mImageEmpty;
    private int mViewMode;
    private MenuItem mViewModeMenu;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private SharedPreferences mPrefs;
    private DataManager mDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.bind(this, v);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        mViewMode = mPrefs.getInt(SP_VIEW_MODE, VIEW_MODE_LIST);
        if (mViewMode == VIEW_MODE_STAGGERED) mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        if (mViewMode == VIEW_MODE_LIST) mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerInsetsDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mRefresher.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        mRefresher.setEnabled(true);

        new DataLoadTask().execute();

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

    @Override
    public void onResume() {
        super.onResume();
        if (mDataManager != null) {
            mDataManager.notifyDataSetChanged();
            mDataManager.checkNewDay();
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class DataLoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mDataManager = DataManager.getInstance(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mRefresher.setEnabled(false);
            if (!DataManager.isEventEmpty(getContext())) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyLayout.setVisibility(View.GONE);
                mRecyclerView.setAdapter(mDataManager.getEventAdapter());
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(R.drawable.empty_bg).into(mImageEmpty);
            }
        }
    }
}
