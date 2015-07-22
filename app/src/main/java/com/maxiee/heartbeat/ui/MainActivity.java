package com.maxiee.heartbeat.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.support.CrashHandler;
import com.maxiee.heartbeat.ui.fragments.EventListFragment;
import com.maxiee.heartbeat.ui.fragments.EventTodayFragment;
import com.maxiee.heartbeat.ui.fragments.LabelCloudFragment;
import com.maxiee.heartbeat.ui.fragments.StatisticsFragment;
import com.quinny898.library.persistentsearch.SearchBox;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton mFab;
    private EventListFragment mEventListFragment;
    private EventTodayFragment mEventTodayFragment;
    private LabelCloudFragment mLabelCloudFragment;
    private StatisticsFragment mStatisticsFragment;
    private SearchBox mSearchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CrashHandler.register(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchBox = (SearchBox) findViewById(R.id.searchbox);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerToggle.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerToggle.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                mDrawerToggle.onDrawerStateChanged(newState);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.abc_action_bar_home_description,
                R.string.abc_action_bar_home_description){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mEventListFragment = new EventListFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.nested_content, mEventListFragment).commit();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.setClass(MainActivity.this, AddEventActivity.class);
                startActivityForResult(i, AddEventActivity.ADD_EVENT_REQUEST);
            }
        });

        mSearchBox.setLogoText(getString(R.string.search_hint));
        mSearchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {

            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                mSearchBox.hideCircularly(MainActivity.this);
            }

            @Override
            public void onSearchTermChanged() {

            }

            @Override
            public void onSearch(String s) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setClass(MainActivity.this, SearchResultActivity.class);
                i.putExtra("search", s);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.setClass(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.action_search) {
            mSearchBox.revealFromMenuItem(R.id.action_search, this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AddEventActivity.ADD_EVENT_RESULT_OK
                || resultCode == EventDetailActivity.EVENT_DETAIL_MODIFIED) {
            if (mEventListFragment != null) {
                mEventListFragment.updateEventList();
            }

            if (mEventTodayFragment != null) {
                mEventTodayFragment.updateEventList();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawer(mNavigationView);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (menuItem.getItemId()) {
            case R.id.nav_event_list:
                if (mEventListFragment == null) {
                    mEventListFragment = new EventListFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.nested_content, mEventListFragment)
                        .commit();
                setTitle(getString(R.string.event_list));
                return true;
            case R.id.nav_today:
                if (mEventTodayFragment == null) {
                    mEventTodayFragment = new EventTodayFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.nested_content, mEventTodayFragment)
                        .commit();
                setTitle(R.string.today);
                return true;
            case R.id.nav_label_cloud:
                if (mLabelCloudFragment == null) {
                    mLabelCloudFragment = new LabelCloudFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.nested_content, mLabelCloudFragment)
                        .commit();
                setTitle(R.string.labelCloud);
                return true;
            case R.id.nav_statistics:
                if (mStatisticsFragment == null) {
                    mStatisticsFragment = new StatisticsFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.nested_content, mStatisticsFragment)
                        .commit();
                setTitle(R.string.statistics);
        }
        return false;
    }
}
