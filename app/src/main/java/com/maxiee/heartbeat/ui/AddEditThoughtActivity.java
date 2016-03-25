package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.GalleryUtils;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.common.BaseActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15-9-15.
 */
public class AddEditThoughtActivity extends BaseActivity {
    private final static String TAG = AddEditThoughtActivity.class.getSimpleName();

    public static final String MODE = "mode";
    public static final String EVENT_KEY = "event_key";
    public static final String THOUGHT_ID = "thought_id";
    public static final String THOUGHT = "thought";
    public static final String TIMESTAMP = "timestamp";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;
    public static final int INVALID_EVENT_KEY = -1;
    public static final long INVALID_THOUGHT_KEY = -1;
    private static final int ADD_IMAGE = 1127;

    @Bind(R.id.toolbar)         Toolbar         mToolbar;
    @Bind(R.id.edit_thought)    EditText        mEditThought;
    @Bind(R.id.image)           ImageView       mImage;
    @Bind(R.id.add_imgae)       ImageButton     mAddImageButton;
    @Bind(R.id.current_date)    TextView        mCurrentDate;
    @Bind(R.id.current_time)    TextView        mCurrentTime;

    private String mTextThought;

    private int mMode;
    private long mEventKey = INVALID_EVENT_KEY;
    private long mThoughtKey = INVALID_THOUGHT_KEY;

    private int mResType = Thoughts.Thought.HAS_NO_RES;
    private String mResPath = "";
    private int mResTypeOld = Thoughts.Thought.HAS_NO_RES;
    private String mResPathOld = "";

    private boolean mExitEnsure = false;

    private long mTimestamp;
    private long mTimestampBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_thought);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mMode = intent.getIntExtra(MODE, MODE_NEW);

        if (mMode == MODE_NEW) {
            mEventKey = intent.getLongExtra(EVENT_KEY, INVALID_EVENT_KEY);
            mTimestamp = System.currentTimeMillis();
            mTimestampBackup = mTimestamp;
        }

        if (mMode == MODE_EDIT) {
            mThoughtKey = intent.getLongExtra(THOUGHT_ID, INVALID_THOUGHT_KEY);
            mTextThought = intent.getStringExtra(THOUGHT);
            mResType = intent.getIntExtra(
                    Thoughts.Thought.THOUGHT_RES,
                    Thoughts.Thought.HAS_NO_RES
            );
            mResPath = intent.getStringExtra(Thoughts.Thought.THOUGHT_PATH);
            mResTypeOld = mResType;
            mResPathOld = mResPath;
            mTimestamp = intent.getLongExtra(TIMESTAMP, System.currentTimeMillis());
            mTimestampBackup = mTimestamp;
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryUtils.openGallery(AddEditThoughtActivity.this);
            }
        });

        if (mMode == MODE_NEW) setTitle(getString(R.string.add_thought));
        if (mMode == MODE_EDIT) setTitle(getString(R.string.dialog_edit_thought));

        if (mMode == MODE_EDIT) initEditView();

        initDate();
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


    private void initEditView() {
        mEditThought.setText(mTextThought);
        if (mResType == Thoughts.Thought.RES_IMAGE) {
            mImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(mResPath).into(mImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMode == MODE_NEW)  getMenuInflater().inflate(R.menu.dialog_new_thought, menu);
        if (mMode == MODE_EDIT) getMenuInflater().inflate(R.menu.dialog_edit_thought, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = GalleryUtils.onActivityResult(this, requestCode, resultCode, data);
        if (path == null) return;
        mImage.setVisibility(View.VISIBLE);
        Glide.with(this).load(path).into(mImage);
        mResType = Thoughts.Thought.RES_IMAGE;
        mResPath = path;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            mTextThought = mEditThought.getText().toString();
            if (!checkThoughtValid()) return true;
            new AddThoughtTask().execute();
        }
        if (id == R.id.done) {
            Log.d(TAG, "Thought edit mode:");
            mTextThought = mEditThought.getText().toString();
            if (!checkThoughtValid()) return true;
            if (mResTypeOld == Thoughts.Thought.HAS_NO_RES &&
                    mResType == Thoughts.Thought.HAS_NO_RES) {
                Log.d(TAG, "Has no res, update thought directly");
                new UpdateThoughtTask().execute(UpdateThoughtTask.RES_DO_NOTHING);
                return true;
            }
            if (mResTypeOld == Thoughts.Thought.HAS_NO_RES &&
                    mResType != Thoughts.Thought.HAS_NO_RES) {
                Log.d(TAG, "No res to has res, insert res");
                new UpdateThoughtTask().execute(UpdateThoughtTask.RES_INSERT);
                return true;
            }
            if (mResTypeOld != Thoughts.Thought.HAS_NO_RES &&
                    mResType != Thoughts.Thought.HAS_NO_RES) {
                if (mResType == mResTypeOld &&
                        mResPathOld.equals(mResPath)) {
                    Log.d(TAG, "Res not change!");
                    new UpdateThoughtTask().execute(UpdateThoughtTask.RES_DO_NOTHING);
                    return true;
                }
                Log.d(TAG, "Res changed, update it.");
                new UpdateThoughtTask().execute(UpdateThoughtTask.RES_UPDATE);
            }
            return true;
        }
        if (id == R.id.delete) {
            ThoughtUtils.deleteByThoughtId(this, mThoughtKey);
            finish();
        }
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

    private boolean checkThoughtValid() {
        if (mTextThought.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.notempty,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (mMode == MODE_NEW && mEventKey == INVALID_EVENT_KEY) {
            Toast.makeText(
                    this,
                    R.string.dataerror,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private class AddThoughtTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ThoughtUtils.addThought(AddEditThoughtActivity.this, mEventKey, mTextThought, mTimestamp, mResType, mResPath);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }

    private class UpdateThoughtTask extends AsyncTask<Integer, Void, Void> {

        public static final int RES_DO_NOTHING =0;
        public static final int RES_INSERT = 1;
        public static final int RES_UPDATE =2;

        @Override
        protected Void doInBackground(Integer... params) {
            int state = params[0];
            Log.d(TAG, "thoughtKey:" + String.valueOf(mThoughtKey));
            Log.d(TAG, "thought:" + mTextThought);
            ThoughtUtils.updateThought(AddEditThoughtActivity.this, mThoughtKey, mTextThought, mTimestamp);
            if (state == RES_DO_NOTHING) return null;
            if (state == RES_INSERT) {
                ThoughtUtils.addRes(AddEditThoughtActivity.this, mThoughtKey, mResType, mResPath);
            }
            if (state == RES_UPDATE) {
                ThoughtUtils.updateRes(AddEditThoughtActivity.this, mThoughtKey, mResType, mResPath);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }
}
