package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.common.ThemeUtils;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.database.utils.LabelUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Image;
import com.maxiee.heartbeat.model.Label;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.common.BaseActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
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

    @Bind(R.id.edit_event)              EditText        mEditEvent;
    @Bind(R.id.first_thought_layout)    TextInputLayout mLayoutFirstThought;
    @Bind(R.id.first_thought)           EditText        mEditFirstThought;
    @Bind(R.id.tagview_added)           TagView         mTagViewRecent;
    @Bind(R.id.tagview_to_add)          TagView         mTagViewToAdd;
    @Bind(R.id.add_imgae)               TextView        mTvAddImage;
    @Bind(R.id.backdrop)                ImageView       mImageBackDrop;
    @Bind(R.id.header)                  View            mHeaderView;
    @Bind(R.id.current_date)            TextView        mCurrentDate;
    @Bind(R.id.current_time)            TextView        mCurrentTime;

    private String mStrEvent;
    private String mStrFirstThought;
    private ArrayList<String> mLabels = new ArrayList<>();
    private Uri mImageUri;
    private DataManager mDataManager;

    private boolean mIsModify = false;
    private boolean mExitEnsure = false;

    private long mEventId;
    private String mStrEventBackup;
    private ArrayList<String> mLabelsBackup = new ArrayList<>();
    private String mImagePath;
    private String mImagePathBackup;
    private boolean mHasImage = false;

    private long mTimestamp;
    private long mTimestampBackup;

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
        // new event or edit event?
        mEventId = i.getLongExtra(ID_EVENT_MODIFY, EVENT_NO_ID);
        if (mEventId != EVENT_NO_ID) { // edit event
            mIsModify = true;
            Event e = EventUtils.getEvent(this, mEventId);
            mStrEventBackup = e.getEvent();
            mEditEvent.setText(mStrEventBackup);
            // TODO hack
            ArrayList<Label> labels = LabelUtils.getLabelsByEvent(this, e);
            for (Label l : labels) mLabels.add(l.getLabel());
            mLabelsBackup = new ArrayList<>(mLabels);
            Image image = ImageUtils.getImageByEventId(this, mEventId);
            if (image != null) {
                mImagePath = image.getPath();
                mTvAddImage.setText(R.string.change_image);
                Glide.with(this).load(mImagePath).into(mImageBackDrop);
                changeHeaderToImage();
                mImagePathBackup = mImagePath;
                mHasImage = true;
            } else {
                mHasImage = false;
            }

            mTimestamp = e.getTimestamp();
            mTimestampBackup = mTimestamp;
        } else { // new event
            mTimestamp = System.currentTimeMillis();
            mTimestampBackup = mTimestamp;
        }

        initDate();

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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            AddEventActivity.this,
                            ThemeUtils.getCurrentDialogTheme(AddEventActivity.this));
                    builder.setTitle(R.string.new_tag);
                    final EditText input = new EditText(AddEventActivity.this);
                    final FrameLayout container = new FrameLayout(AddEventActivity.this);
                    final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(40, 40, 40, 40);
                    container.addView(input, params);
                    builder.setView(container);
                    builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String label = input.getText().toString();
                            if (label.isEmpty()) return;
                            if (!mLabels.contains(label)) mLabels.add(label);
                            initTagsToAdd();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
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

    private void initDate() {
        updateDate();
        mCurrentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(mTimestamp);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mTimestamp = TimeUtils.updateTimestampWithDate(year, monthOfYear, dayOfMonth, mTimestamp);
                                updateDate();
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), getString(R.string.date));
            }
        });

        mCurrentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(mTimestamp);
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                mTimestamp = TimeUtils.updateTimestampWithTime(hourOfDay, minute, second, mTimestamp);
                                updateDate();
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        cal.get(Calendar.SECOND),
                        true);
                tpd.show(getFragmentManager(), getString(R.string.date));
            }
        });
    }

    private void updateDate() {
        mCurrentDate.setText(TimeUtils.parseDateDate(this, mTimestamp));
        mCurrentTime.setText(TimeUtils.parseDateTime(this, mTimestamp));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(data.getData()).into(mImageBackDrop);
            mHasImage = true;
            changeHeaderToImage();
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

    private void changeHeaderToImage() {
        mHeaderView.setVisibility(View.GONE);
        mImageBackDrop.setVisibility(View.VISIBLE);
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
        Map<Long, Integer> recentLabels = LabelUtils.getFreq(this);
        if (recentLabels == null) {
            return;
        }
        ArrayList<Map.Entry<Long,Integer>> list = new ArrayList<>(recentLabels.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Long, Integer>>() {
            @Override
            public int compare(Map.Entry<Long, Integer> lhs, Map.Entry<Long, Integer> rhs) {
                return rhs.getValue() - lhs.getValue();
            }
        });
        for (Map.Entry<Long,Integer> labelId : list) {
            Tag tag = new Tag(LabelUtils.getLabelByLabelId(this, labelId.getKey()).getLabel());
            tag.hasExtraInfo = true;
            tag.extraInfoString = " x" + String.valueOf(labelId.getValue());
            tag.layoutColor = getResources().getColor(R.color.tag_gray);
            tag.tagTextSize = 10.0f;
            mTagViewRecent.addTag(tag);
        }
    }

    private class AddEventTask extends AsyncTask<Void, Void, Void> {
        private long mEventKey;

        @Override
        protected Void doInBackground(Void... params) {

            // add event
            Event newEvent = EventUtils.addEvent(AddEventActivity.this, mStrEvent, mTimestamp);
            mEventKey = newEvent.getId();

            // add thought
            if (!mStrFirstThought.isEmpty()) {
                ThoughtUtils.addThought(AddEventActivity.this, mEventKey, mStrFirstThought, mTimestamp, Thoughts.Thought.HAS_NO_RES, Thoughts.Thought.HAS_NO_PATH);
            }

            // add labels
            LabelUtils.addLabels(AddEventActivity.this, mEventKey, mLabels);

            if (mImageUri != null) {
                // convert uri to path
                String path = FileUtils.uriToPath(AddEventActivity.this, mImageUri);
                ImageUtils.addImage(AddEventActivity.this, mEventKey, path);
            }

            mDataManager.addEvent(newEvent);

            Log.d(TAG, "添加事件");
            Log.d(TAG, "id: " + String.valueOf(mEventKey));
            Log.d(TAG, "labels: " + mLabels.toString());
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
            if (!mStrEventBackup.equals(mStrEvent) || mTimestamp != mTimestampBackup) {
                // TODO temp Event
                Event e = new Event(mEventId, mStrEvent, mTimestamp);
                EventUtils.updateEvent(AddEventActivity.this, e);
            }
            if (mHasImage && !mImagePath.equals(mImagePathBackup)) {
                // update
                ImageUtils.updateImageByEventId(AddEventActivity.this, mEventId, mImagePath);
            } else if (!mHasImage && mImagePath != null) {
                // add
                ImageUtils.addImage(AddEventActivity.this, mEventId, mImagePath);
            }
            for (String l: mLabels) {
                long labelKey = LabelUtils.hasLabel(AddEventActivity.this, l);
                if (labelKey == LabelUtils.NOT_FOUND) {
                    LabelUtils.addLabel(AddEventActivity.this, mEventId, l);
                } else {
                    // TODO temp label
                    Label label = new Label(labelKey, "");
                    ArrayList<Event> events = EventUtils.getEvents(AddEventActivity.this, label);
                    boolean alreadyHas = false;
                    for (Event event:events)
                        if (event.getId() == mEventId)
                            alreadyHas = true;
                    if (!alreadyHas) {
                        LabelUtils.addRelation(AddEventActivity.this, mEventId, labelKey);
                    }
                }
            }
            for (String l:mLabelsBackup)
                if (!mLabels.contains(l)) {
                    long key = LabelUtils.hasLabel(AddEventActivity.this, l);
                    if (key != LabelUtils.NOT_FOUND) {
                        LabelUtils.deleteRelation(AddEventActivity.this, mEventId, key);
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
