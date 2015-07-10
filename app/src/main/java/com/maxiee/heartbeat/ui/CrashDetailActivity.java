package com.maxiee.heartbeat.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.model.Crash;

/**
 * Created by maxiee on 15-7-11.
 */
public class CrashDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTvCrash;
    private String mCrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvCrash = (TextView) findViewById(R.id.tv_crash);

        Intent intent = getIntent();
        mCrash = intent.getStringExtra(Crash.LOG);
        mTvCrash.setText(mCrash);

        setTitle(getString(R.string.settings_crash));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crash_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.clip_board) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("crash report", mCrash);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(CrashDetailActivity.this, getString(R.string.copy_finished), Toast.LENGTH_LONG).show();
        }
        if(id == R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mCrash);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
