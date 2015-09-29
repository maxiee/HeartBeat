package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.database.api.AddThoughtApi;
import com.maxiee.heartbeat.database.api.AddThoughtResByKeyApi;
import com.maxiee.heartbeat.database.api.DeleteThoughtByKeyApi;
import com.maxiee.heartbeat.database.api.UpdateThoughtApi;
import com.maxiee.heartbeat.database.api.UpdateThoughtResByKey;
import com.maxiee.heartbeat.model.Thoughts;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by maxiee on 15-9-15.
 */
public class AddEditThoughtActivity extends AppCompatActivity {
    private final static String TAG = AddEditThoughtActivity.class.getSimpleName();

    public static final String MODE = "mode";
    public static final String EVENT_KEY = "event_key";
    public static final String THOUGHT_ID = "thought_id";
    public static final String THOUGHT = "thought";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;
    public static final int INVALID_EVENT_KEY = -1;
    public static final int INVALID_THOUGHT_KEY = 1;
    private static final int ADD_IMAGE = 1127;

    private Toolbar mToolbar;
    private EditText mEditThought;
    private ImageView mImage;
    private String mTextThought;

    private int mMode;
    private int mEventKey = INVALID_EVENT_KEY;
    private int mThoughtKey = INVALID_THOUGHT_KEY;

    private int mResType = Thoughts.Thought.HAS_NO_RES;
    private String mResPath = "";
    private int mResTypeOld = Thoughts.Thought.HAS_NO_RES;
    private String mResPathOld = "";

    private boolean mExitEnsure = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_thought);

        Intent intent = getIntent();
        mMode = intent.getIntExtra(MODE, MODE_NEW);

        if (mMode == MODE_NEW) {
            mEventKey = intent.getIntExtra(EVENT_KEY, INVALID_EVENT_KEY);
        }

        if (mMode == MODE_EDIT) {
            mThoughtKey = intent.getIntExtra(THOUGHT_ID, INVALID_THOUGHT_KEY);
            mTextThought = intent.getStringExtra(THOUGHT);
            mResType = intent.getIntExtra(
                    Thoughts.Thought.THOUGHT_RES,
                    Thoughts.Thought.HAS_NO_RES
            );
            mResPath = intent.getStringExtra(Thoughts.Thought.THOUGHT_PATH);
            mResTypeOld = mResType;
            mResPathOld = mResPath;
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = (ImageView) findViewById(R.id.image);
        mEditThought = (EditText) findViewById(R.id.edit_thought);

        if (mMode == MODE_NEW) setTitle(getString(R.string.add_thought));
        if (mMode == MODE_EDIT) setTitle(getString(R.string.dialog_edit_thought));

        if (mMode == MODE_EDIT) initEditView();
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
        if (requestCode == ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            mImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(data.getData()).into(mImage);
            mResType = Thoughts.Thought.RES_IMAGE;
            mResPath = FileUtils.uriToPath(this, data.getData());
        }
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
        if (id == R.id.add_imgae) {
            if (Build.VERSION.SDK_INT < 19) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(i, getString(R.string.add_image)),
                        ADD_IMAGE);
            } else {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(i, getString(R.string.add_image)),
                        ADD_IMAGE);
            }
            return true;
        }
        if (id == R.id.delete) {
            new DeleteThoughtByKeyApi(this, mThoughtKey).exec();
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
            new AddThoughtApi(
                    AddEditThoughtActivity.this,
                    mEventKey,
                    mTextThought,
                    mResType,
                    mResPath
            ).exec();
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
            new UpdateThoughtApi(
                    AddEditThoughtActivity.this,
                    mThoughtKey,
                    mTextThought
            ).exec();
            if (state == RES_DO_NOTHING) return null;
            if (state == RES_INSERT) {
                new AddThoughtResByKeyApi(
                        AddEditThoughtActivity.this,
                        mThoughtKey,
                        mResType,
                        mResPath).exec();
            }
            if (state == RES_UPDATE) {
                new UpdateThoughtResByKey(
                        AddEditThoughtActivity.this,
                        mThoughtKey,
                        mResType,
                        mResPath
                ).exec();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }
}
