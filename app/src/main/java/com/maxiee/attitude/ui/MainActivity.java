package com.maxiee.attitude.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maxiee.attitude.R;
import com.maxiee.attitude.ui.fragments.EventListFragment;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mFab;
    private EventListFragment mEventListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
}
