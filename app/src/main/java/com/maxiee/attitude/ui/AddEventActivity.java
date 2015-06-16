package com.maxiee.attitude.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.attitude.R;
import com.maxiee.attitude.common.DialogAsyncTask;
import com.maxiee.attitude.database.api.AddEventApi;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-11.
 */
public class AddEventActivity extends AppCompatActivity{

    public final static int ADD_EVENT_REQUEST = 100;
    public final static int ADD_EVENT_RESULT_OK = 101;

    private EditText mEditEvent;
    private EditText mEditFirstThought;
    private String mStrEvent;
    private String mStrFirstThought;
    private ArrayList<String> mLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditEvent = (EditText) findViewById(R.id.edit_event);
        mEditFirstThought = (EditText) findViewById(R.id.first_thought);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrEvent = mEditEvent.getText().toString();
                mStrFirstThought = mEditFirstThought.getText().toString();

                if (mStrEvent.isEmpty() || mStrFirstThought.isEmpty()) {
                    Toast.makeText(AddEventActivity.this,
                            getString(R.string.notempty),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                new AddEventTask(AddEventActivity.this).execute();

            }
        });

    }

    private class AddEventTask extends DialogAsyncTask {

        public AddEventTask(Context context) {
            super(context);
        }

        @Override
        public void onFinish() {
            if (mTaskSuccess) {
                setResult(ADD_EVENT_RESULT_OK);
            }
            finish();
        }

        @Override
        protected String doInBackground(Void... params) {
            mLabels = new ArrayList<String>();
            mLabels.add("label1");
            mLabels.add("label2");
            mLabels.add("label3");

            try {
                new AddEventApi(
                        AddEventActivity.this,
                        mStrEvent,
                        mStrFirstThought,
                        mLabels).exec();
                mTaskSuccess = true;
                return getmContext().getString(R.string.add_ok);
            } catch (JSONException e) {
                e.printStackTrace();
                mTaskSuccess = false;
                return getmContext().getString(R.string.add_failed);
            }
        }
    }

}
