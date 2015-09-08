package com.maxiee.heartbeat.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.GetImageByEventKeyApi;

/**
 * Created by maxiee on 15-8-26.
 */
public class GalleryActivity extends AppCompatActivity {
    public static final String EVENT_ID = "event_id";
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        int eventId = getIntent().getIntExtra(EVENT_ID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = (ImageView) findViewById(R.id.imageview);

        if (eventId != -1) {
            final String imagePath = new GetImageByEventKeyApi(this, eventId).exec();
            Glide.with(this).load(imagePath).into(mImage);
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
