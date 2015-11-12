package com.maxiee.heartbeat.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.ui.common.BaseActivity;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by maxiee on 15-8-26.
 */
public class GalleryActivity extends BaseActivity {
    public static final String EVENT_ID = "event_id";
    public static final String PATH = "path";
    private PhotoView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        long eventId = getIntent().getLongExtra(EVENT_ID, -1);
        String path = "";
        if (eventId == -1) {
            path = getIntent().getStringExtra(PATH);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setTitle("");
        toolbar.setAlpha(0.6f);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = (PhotoView) findViewById(R.id.imageview);

        mImage.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });

        if (eventId != -1) {
            String imagePath = ImageUtils.getImageByEventId(this, eventId).getPath();
            Glide.with(this).load(imagePath).into(mImage);
        } else {
            Glide.with(this).load(path).into(mImage);
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
