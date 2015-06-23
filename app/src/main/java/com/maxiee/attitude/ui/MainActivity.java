package com.maxiee.attitude.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maxiee.attitude.R;
import com.maxiee.attitude.ui.fragments.EventListFragment;
import com.maxiee.attitude.ui.fragments.EventTodayFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private FloatingActionButton mFab;
    private EventListFragment mEventListFragment;
    private EventTodayFragment mEventTodayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AddEventActivity.ADD_EVENT_RESULT_OK) {
            mEventListFragment.updateEventList();
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
        }
        return false;
    }
}
