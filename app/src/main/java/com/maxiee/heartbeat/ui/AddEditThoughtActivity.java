package com.maxiee.heartbeat.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.AddThoughtApi;
import com.maxiee.heartbeat.model.Thoughts;

/**
 * Created by maxiee on 15-9-15.
 */
public class AddEditThoughtActivity extends AppCompatActivity {
    public static final String MODE = "mode";
    public static final String EVENT_KEY = "event_key";
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;
    private static final int INVALID_EVENT_KEY = -1;

    private Toolbar mToolbar;
    private Toolbar mTools;
    private EditText mEditThought;
    private String mTextThought;

    private int mMode;
    private int mEventKey;

    private int mResType = Thoughts.Thought.HAS_NO_RES;
    private String mResPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_thought);

        Intent intent = getIntent();
        mMode = intent.getIntExtra(MODE, MODE_NEW);
        mEventKey = intent.getIntExtra(EVENT_KEY, INVALID_EVENT_KEY);

        if (mMode == MODE_EDIT) {
            mResType = intent.getIntExtra(
                    Thoughts.Thought.THOUGHT_RES,
                    Thoughts.Thought.HAS_NO_RES
            );
            mResPath = intent.getStringExtra(Thoughts.Thought.THOUGHT_PATH);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTools = (Toolbar) findViewById(R.id.tools);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditThought = (EditText) findViewById(R.id.edit_thought);

        if (mMode == MODE_NEW) setTitle(getString(R.string.add_thought));
        if (mMode == MODE_EDIT) setTitle(getString(R.string.dialog_edit_thought));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMode == MODE_NEW)  getMenuInflater().inflate(R.menu.dialog_new_thought, menu);
        if (mMode == MODE_EDIT) getMenuInflater().inflate(R.menu.dialog_edit_thought, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            mTextThought = mEditThought.getText().toString();
            if (mTextThought.isEmpty()) {
                Toast.makeText(
                        this,
                        R.string.notempty,
                        Toast.LENGTH_LONG).show();
                return true;
            }
            if (mEventKey == INVALID_EVENT_KEY) {
                Toast.makeText(
                        this,
                        R.string.dataerror,
                        Toast.LENGTH_LONG).show();
            }
            new AddThoughtTask().execute();
        }
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
