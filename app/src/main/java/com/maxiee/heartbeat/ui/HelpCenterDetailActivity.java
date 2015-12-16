package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.ui.common.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15/12/16.
 */
public class HelpCenterDetailActivity extends BaseActivity {
    public static final String TITLE = "title";
    public static final String FILENAME = "filename";
    private static final String BASE_URL = "file:///android_asset/";

    @Bind(R.id.toolbar)     Toolbar     mToolbar;
    @Bind(R.id.web)         WebView     mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center_detail);
        ButterKnife.bind(this);

        Intent i = getIntent();
        String title = i.getStringExtra(TITLE);
        String filename = i.getStringExtra(FILENAME);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);

        if (!filename.isEmpty()) {
            mWebView.loadUrl(BASE_URL + filename);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
