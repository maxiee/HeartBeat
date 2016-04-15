package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.backup.BackupManager;
import com.maxiee.heartbeat.data.DataManager;
import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.database.utils.LabelUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Label;
import com.maxiee.heartbeat.model.Thoughts;

/**
 * Created by maxiee on 15-7-29.
 */
public class EntryActivity extends Activity {

    public static final String TAG = EntryActivity.class.getSimpleName();
    public static final String IS_FIRST_USE = "is_first";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        if (BackupManager.needTransGallery(EntryActivity.this)) {
            Log.d(TAG, "Need trans!");
            new TransGalleryTask().execute();
        } else {
            Log.d(TAG, "Need not trans!");
            new StartTask().execute();
        }
    }

    private boolean isFirstUse() {
        SharedPreferences sp = getSharedPreferences("hb", MODE_PRIVATE);
        boolean isFirst = sp.getBoolean(IS_FIRST_USE, true);
        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(IS_FIRST_USE, false);
            editor.apply();
            return true;
        }
        return false;
    }

    private class TransGalleryTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EntryActivity.this);
            progressDialog.setMessage(getString(R.string.trans_gallery));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            BackupManager.transGallery(EntryActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.cancel();
            new StartTask().execute();
        }
    }

    private class StartTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            // 首次使用添加引导教程
            if (DataManager.isEventEmpty(EntryActivity.this) && isFirstUse()) {
                Log.d("maxiee", "捕捉到一只新用户!生成引导教程...");
                addTutorial();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // 手势解锁
            SharedPreferences sp = getSharedPreferences("hb", Context.MODE_PRIVATE);
            String pattern = sp.getString("pattern", "");
            if (pattern.isEmpty()) {
                Intent i = new Intent(EntryActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(EntryActivity.this, PatternActivity.class);
                i.putExtra(PatternActivity.ACTION, PatternActivity.VERIFY);
                startActivity(i);
                finish();
            }
        }
    }

    private void addTutorial() {

        Label label = LabelUtils.addLabel(this, getString(R.string.app_name));
        long eventKey;

        // Tut4
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_4_event), System.currentTimeMillis()).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_4_thought_4),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );

        // Tut3
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_3_event), System.currentTimeMillis()).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_3_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_3_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );

        // Tut2
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_2_event), System.currentTimeMillis()).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_2_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_2_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );

        // Tut1
        eventKey = EventUtils.addEvent(this, getString(R.string.tut_1_event), System.currentTimeMillis()).getId();
        LabelUtils.addRelation(this, eventKey, label.getId());
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_1_thought_1),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_1_thought_2),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
        ThoughtUtils.addThought(
                this, eventKey,
                getString(R.string.tut_1_thought_3),
                Thoughts.Thought.HAS_NO_RES,
                Thoughts.Thought.HAS_NO_PATH
        );
    }
}
