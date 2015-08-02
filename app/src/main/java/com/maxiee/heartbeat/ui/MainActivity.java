package com.maxiee.heartbeat.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.TypedValue;
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
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SEARCH_HISTORY_SIZE = 5;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private ViewPagerAdapter mViewPagerAdapter;
    private SearchBox mSearchBox;
    private ArrayList<String> mSearchHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CrashHandler.register(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("");

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                setTitle(mViewPagerAdapter.getFragmentTitle(tab.getPosition()));
                tab.setContentDescription(
                        mViewPagerAdapter.getPageIcon(
                                tab.getPosition(),
                                getThemeAccentColor(MainActivity.this)));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setContentDescription(
                        mViewPagerAdapter.getPageIcon(
                                tab.getPosition(),
                                getResources().getColor(R.color.tab_white_trans)
                        )
                );
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // init TabLayout
        setTitle(mViewPagerAdapter.getFragmentTitle(0));
        tabLayout.getTabAt(0).setContentDescription(
                mViewPagerAdapter.getPageIcon(
                        0,
                        getThemeAccentColor(this)
                )
        );

        mSearchBox = (SearchBox) findViewById(R.id.searchbox);

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

        mSearchHistory = new ArrayList<>();
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
                if (mSearchHistory.size() == SEARCH_HISTORY_SIZE) {
                    mSearchHistory.remove(SEARCH_HISTORY_SIZE - 1);
                }
                if (!mSearchHistory.contains(s)) {
                    mSearchHistory.add(0, s);
                }
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setClass(MainActivity.this, SearchResultActivity.class);
                i.putExtra("search", s);
                startActivity(i);
            }
        });
    }

    private static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        return value.data;
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFrag(new EventListFragment(), getString(R.string.event_list), R.drawable.ic_action_reorder);
        mViewPagerAdapter.addFrag(new EventTodayFragment(), getString(R.string.today), R.drawable.ic_action_today);
        mViewPagerAdapter.addFrag(new LabelCloudFragment(), getString(R.string.labelCloud), R.drawable.ic_action_label);
        mViewPagerAdapter.addFrag(new StatisticsFragment(), getString(R.string.statistics), R.drawable.ic_action_trending_up);
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
            mSearchBox.clearSearchable();
            for (String his: mSearchHistory) {
                mSearchBox.addSearchable(
                        new SearchResult(his, null));
            }
            mSearchBox.revealFromMenuItem(R.id.action_search, this);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: put these code to onResume!
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Integer> mFragmentIconList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title, @DrawableRes int id) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mFragmentIconList.add(id);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPageIcon(position, getResources().getColor(R.color.tab_white_trans));
        }

        public CharSequence getPageIcon(int position, int filterColor) {
            Drawable image = getDrawable(mFragmentIconList.get(position));
            image.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        public String getFragmentTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
