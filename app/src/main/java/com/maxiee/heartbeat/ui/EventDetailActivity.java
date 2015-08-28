package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.database.api.AddImageApi;
import com.maxiee.heartbeat.database.api.DeleteImageByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetAllThoughtApi;
import com.maxiee.heartbeat.database.api.GetImageByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetLabelsByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetOneEventApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.ui.adapter.ThoughtTimeaxisAdapter;
import com.maxiee.heartbeat.ui.dialog.EditEventDialog;
import com.maxiee.heartbeat.ui.dialog.NewThoughtDialog;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-13.
 */
public class EventDetailActivity extends AppCompatActivity {
    private final static String TAG = EventDetailActivity.class.getSimpleName();

    public final static int EVENT_DETAIL = 200;
    public final static int EVENT_DETAIL_MODIFIED = 201;

    private static final int ADD_IMAGE = 1127;

    private Event mEvent;

    private TextView mTvEvent;
    private RecyclerView mRecyclerView;
    private ThoughtTimeaxisAdapter mAdapter;
    private TagView mTagView;
    private TextView mTvTime;
    private ImageView mImageBackDrop;
    private View mCardEvent;
    private int mId;
    private TextView mAddImageText;

    public static final String EXTRA_NAME = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        mId = intent.getIntExtra(EXTRA_NAME, -1);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvEvent = (TextView) findViewById(R.id.tv_event);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mTagView = (TagView) findViewById(R.id.tagview);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mImageBackDrop = (ImageView) findViewById(R.id.backdrop);
        mCardEvent = (View) findViewById(R.id.card_event);
        mAddImageText = (TextView) findViewById(R.id.add_imgae);

        mEvent =  new GetOneEventApi(this, mId).exec();
        mTvEvent.setText(mEvent.getmEvent());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ThoughtTimeaxisAdapter(
                new GetAllThoughtApi(this, mEvent.getmId()).exec()
        );
        mRecyclerView.setAdapter(mAdapter);

        updateTagView();

        mTagView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.setClass(EventDetailActivity.this, LabelDetailActivity.class);
                i.putExtra("tag_text", tag.text);
                startActivity(i);
            }
        });

        mTvTime.setText(TimeUtils.parseTime(this, mEvent.getTimestamp()));

        mCardEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditEventDialog dialog = new EditEventDialog(
                        EventDetailActivity.this,
                        mEvent
                );
                dialog.setOnEditFinishedListener(new EditEventDialog.OnEditFinishedListener() {
                    @Override
                    public void update(String event) {
                        mTvEvent.setText(event);
                        updateTagView();
                        setResult(EVENT_DETAIL_MODIFIED);
                    }

                    @Override
                    public void remove() {
                        setResult(EVENT_DETAIL_MODIFIED);
                        finish();
                    }
                });
                dialog.show();
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewThoughtDialog dialog = new NewThoughtDialog(EventDetailActivity.this, mId);
                dialog.setOnAddFinishedListener(new NewThoughtDialog.OnAddFinishedListener() {
                    @Override
                    public void update() {
                        mEvent = new GetOneEventApi(EventDetailActivity.this, mId).exec();
                        mAdapter = new ThoughtTimeaxisAdapter(
                                new GetAllThoughtApi(EventDetailActivity.this, mEvent.getmId()).exec()
                        );
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
                dialog.show();
            }
        });

        initImage();
    }

    private void initImage() {
        final String imageUri = new GetImageByEventKeyApi(this, mEvent.getmId()).exec();
        if (imageUri == null) {
            mAddImageText.setVisibility(View.VISIBLE);
            mImageBackDrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT < 19) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.add_image)), ADD_IMAGE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.add_image)), ADD_IMAGE);
                    }
                }
            });
            return;
        }
        if (checkUriValid(imageUri)) {
            mAddImageText.setVisibility(View.INVISIBLE);
            Glide.with(this)
                    .load(Uri.parse(imageUri))
                    .into(mImageBackDrop);
            mImageBackDrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EventDetailActivity.this, GalleryActivity.class);
                    i.putExtra(GalleryActivity.EVENT_ID, mEvent.getmId());
                    startActivity(i);
                }
            });
        } else {    // uri not valid
            // toast
            Toast.makeText(this, getString(R.string.uri_parse_filed), Toast.LENGTH_LONG).show();
            // delete invalid entry in database
            new DeleteImageByEventKeyApi(this, mEvent.getmId()).exec();
            // recursive call initImage to init as no images
            initImage();
        }
    }

    // fix bug previous version 0.7.7
    private boolean checkUriValid(String uri) {
        try {
            Cursor cursor = getContentResolver().query(
                    Uri.parse(uri),
                    new String[] {MediaStore.Images.Media.DATA},
                    null, null, null
            );
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        } catch (SecurityException e) {
            return false;
        }
        return false;
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

    public void updateTagView() {
        mTagView.clear();
        ArrayList<String> labels = new GetLabelsByEventKeyApi(this, mEvent.getmId()).exec();
        if (labels != null) {
            for (String label: labels) {
                mTagView.addTag(new Tag(label));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(data.getData()).into(mImageBackDrop);
            Uri mImageUri = data.getData();
            if (Build.VERSION.SDK_INT >= 19) {
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //noinspection ResourceType
                getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
            }
            new AddImageApi(EventDetailActivity.this, mEvent.getmId(), mImageUri.toString()).exec();
            initImage();
        }
    }
}
