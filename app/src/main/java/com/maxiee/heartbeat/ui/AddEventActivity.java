package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.api.AddEventApi;
import com.maxiee.heartbeat.database.api.AddEventLabelRelationApi;
import com.maxiee.heartbeat.database.api.AddImageApi;
import com.maxiee.heartbeat.database.api.AddLabelsApi;
import com.maxiee.heartbeat.database.api.AddThoughtApi;
import com.maxiee.heartbeat.database.api.DeleteEventLabelRelationApi;
import com.maxiee.heartbeat.database.api.GetEventsByLabelKeyApi;
import com.maxiee.heartbeat.database.api.GetImageByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetLabelsAndFreqApi;
import com.maxiee.heartbeat.database.api.GetLabelsByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetOneEventApi;
import com.maxiee.heartbeat.database.api.GetOneLabelApi;
import com.maxiee.heartbeat.database.api.HasLabelApi;
import com.maxiee.heartbeat.database.api.UpdateEventApi;
import com.maxiee.heartbeat.database.api.UpdateImageByKeyApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.common.BaseActivity;
import com.maxiee.heartbeat.ui.dialog.NewLabelDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15-6-11.
 */
public class AddEventActivity extends BaseActivity{

    private final static String TAG = AddEventActivity.class.getSimpleName();
    public static final String ID_EVENT_MODIFY = "id";

    private static final int ADD_IMAGE = 1127;

    public final static int ADD_EVENT_REQUEST = 100;
    public final static int EVENT_NO_ID = -1;

    @Bind(R.id.edit_event)              EditText mEditEvent;
    @Bind(R.id.first_thought_layout)    TextInputLayout mLayoutFirstThought;
    @Bind(R.id.first_thought)           EditText mEditFirstThought;
    @Bind(R.id.tagview_added)           TagView mTagViewRecent;
    @Bind(R.id.tagview_to_add)          TagView mTagViewToAdd;
    @Bind(R.id.add_imgae)               TextView mTvAddImage;
    @Bind(R.id.backdrop)                ImageView mImageBackDrop;

    private String mStrEvent;
    private String mStrFirstThought;
    private ArrayList<String> mLabels = new ArrayList<>();
    private Uri mImageUri;
    private DataManager mDataManager;

    private boolean mIsModify = false;
    private boolean mExitEnsure = false;

    private int mEventId;
    private String mStrEventBackup;
    private ArrayList<String> mLabelsBackup = new ArrayList<>();
    private String mImagePath;
    private String mImagePathBackup;
    private boolean mHasImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        Intent i = getIntent();
        mEventId = i.getIntExtra(ID_EVENT_MODIFY, EVENT_NO_ID);
        if (mEventId != EVENT_NO_ID) {
            mIsModify = true;
            Event e = new GetOneEventApi(this, mEventId).exec();
            mStrEventBackup = e.getmEvent();
            mEditEvent.setText(mStrEventBackup);
            mLabels = new GetLabelsByEventKeyApi(this, mEventId).exec();
            if (mLabels == null) mLabels = new ArrayList<>();
            mLabelsBackup = new ArrayList<>(mLabels);
            mImagePath = new GetImageByEventKeyApi(this, mEventId).exec();
            if (mImagePath != null) {
                mTvAddImage.setText(R.string.change_image);
                Glide.with(this).load(mImagePath).into(mImageBackDrop);
                mImagePathBackup = mImagePath;
                mHasImage = true;
            } else {
                mHasImage = false;
            }
        }

        if (mIsModify) mLayoutFirstThought.setVisibility(View.GONE);

        mDataManager = DataManager.getInstance(this);

        mTvAddImage.setOnClickListener(new View.OnClickListener() {
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

        initTagsToAdd();
        initTagsRecent();

        mTagViewToAdd.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if (tag.text.equals(getString(R.string.new_tag))) {
                    NewLabelDialog dialog = new NewLabelDialog(AddEventActivity.this);
                    dialog.setOnAddFinishedListener(new NewLabelDialog.OnAddFinishedListener() {
                        @Override
                        public void addLabel(String label) {
                            if (!mLabels.contains(label)) mLabels.add(label);
                            initTagsToAdd();
                        }
                    });
                    dialog.show();
                }
            }
        });

        mTagViewToAdd.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(Tag tag, int position) {
                if (!mLabels.isEmpty()) {
                    mLabels.remove(tag.text);
                }
            }
        });

        mTagViewRecent.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if (mLabels.contains(tag.text)) {
                    return;
                }
                mLabels.add(tag.text);
                initTagsToAdd();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrEvent = mEditEvent.getText().toString();
                mStrFirstThought = mEditFirstThought.getText().toString();
                if (mStrEvent.isEmpty()) {
                    Toast.makeText(AddEventActivity.this,
                            getString(R.string.notempty),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mIsModify) {
                    new AddEventTask().execute();
                } else {
                    new UpdateEventTask().execute();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(data.getData()).into(mImageBackDrop);
            mImageUri = data.getData();
            mTvAddImage.setText(R.string.change_image);
            if (Build.VERSION.SDK_INT >= 19) {
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //noinspection ResourceType
                getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
            }
            if (mIsModify) {
                mImagePath = FileUtils.uriToPath(AddEventActivity.this, mImageUri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ensureExit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ensureExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ensureExit() {
        if (!mExitEnsure) {
            mExitEnsure = true;
            Toast.makeText(this, getString(R.string.exit_next_time), Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mExitEnsure = false;
                }
            }, 2000);
        } else {
            this.onBackPressed();
        }
    }

    public void initTagsToAdd() {
        final Tag newTag = new Tag(getString(R.string.new_tag));
        mTagViewToAdd.clear();
        mTagViewToAdd.addTag(newTag);
        for (String tag: mLabels) {
            Tag useTag = new Tag(tag);
            useTag.isDeletable = true;
            mTagViewToAdd.addTag(useTag);
        }
    }

    public void initTagsRecent() {
        Map<Integer, Integer> recentLabels =
                new GetLabelsAndFreqApi(this).exec();
        if (recentLabels == null) {
            return;
        }
        ArrayList<Map.Entry<Integer,Integer>> list = new ArrayList<>(recentLabels.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> lhs, Map.Entry<Integer, Integer> rhs) {
                return rhs.getValue() - lhs.getValue();
            }
        });
        for (Map.Entry<Integer,Integer> labelId : list) {
            Tag tag = new Tag(
                    new GetOneLabelApi(this, labelId.getKey()).exec()
            );
            tag.hasExtraInfo = true;
            tag.extraInfoString = " x" + String.valueOf(labelId.getValue());
            tag.layoutColor = getResources().getColor(R.color.tag_gray);
            tag.tagTextSize = 10.0f;
            mTagViewRecent.addTag(tag);
        }
    }

    private class AddEventTask extends AsyncTask<Void, Void, Void> {
        private int mEventKey;

        @Override
        protected Void doInBackground(Void... params) {

            // add event
            mEventKey = (int) new AddEventApi(
                    AddEventActivity.this,
                    mStrEvent).exec();

            // add thought
            if (!mStrFirstThought.isEmpty()) {
                new AddThoughtApi(
                        AddEventActivity.this,
                        mEventKey,
                        mStrFirstThought,
                        Thoughts.Thought.HAS_NO_RES,
                        Thoughts.Thought.HAS_NO_PATH
                ).exec();
            }

            // add labels
            ArrayList<Integer> labelsKey = new AddLabelsApi(
                    AddEventActivity.this,
                    mLabels
            ).exec();

            for (int labelkey: labelsKey) {
                new AddEventLabelRelationApi(
                        AddEventActivity.this,
                        mEventKey,
                        labelkey
                ).exec();
            }

            if (mImageUri != null) {
                // convert uri to path
                String path = FileUtils.uriToPath(AddEventActivity.this, mImageUri);
                new AddImageApi(AddEventActivity.this, mEventKey, path).exec();
            }

            Event newEvent = new GetOneEventApi(AddEventActivity.this, mEventKey).exec();
            if(newEvent != null) {
                mDataManager.addEvent(newEvent);
            }

            Log.d(TAG, "添加事件");
            Log.d(TAG, "id: " + String.valueOf(mEventKey));
            Log.d(TAG, "labels: " + mLabels.toString());
            Log.d(TAG, "labels_key: " + labelsKey.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent(AddEventActivity.this, EventDetailActivity.class);
            i.putExtra(EventDetailActivity.EXTRA_NAME, mEventKey);
            startActivity(i);
            finish();
        }
    }

    private class UpdateEventTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (!mStrEventBackup.equals(mStrEvent)) {
                new UpdateEventApi(AddEventActivity.this, mEventId, mStrEvent).exec();
            }
            if (mHasImage && !mImagePath.equals(mImagePathBackup)) {
                // update
                new UpdateImageByKeyApi(AddEventActivity.this, mEventId, mImagePath).exec();
            } else if (!mHasImage && mImagePath != null) {
                // add
                new AddImageApi(AddEventActivity.this, mEventId, mImagePath).exec();
            }
            for (String l: mLabels) {
                int labelKey = new HasLabelApi(AddEventActivity.this, l).exec();
                if (labelKey == HasLabelApi.NOT_FOUND) {
                    ArrayList<Integer> labelId = new AddLabelsApi(AddEventActivity.this, l).exec();
                    new AddEventLabelRelationApi(AddEventActivity.this, mEventId, labelId.get(0)).exec();
                } else {
                    ArrayList<Event> events = new GetEventsByLabelKeyApi(AddEventActivity.this, labelKey).exec();
                    boolean alreadyHas = false;
                    for (Event event:events)
                        if (event.getmId() == mEventId)
                            alreadyHas = true;
                    if (!alreadyHas)
                        new AddEventLabelRelationApi(AddEventActivity.this, mEventId, labelKey).exec();
                }
            }
            for (String l:mLabelsBackup)
                if (!mLabels.contains(l)) {
                    int key = new HasLabelApi(AddEventActivity.this, l).exec();
                    if (key != HasLabelApi.NOT_FOUND) {
                        new DeleteEventLabelRelationApi(AddEventActivity.this, mEventId, key).exec();
                    }
                }
            mDataManager.updateEvent(mEventId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }
}
