package com.maxiee.attitude.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.attitude.R;
import com.maxiee.attitude.common.DialogAsyncTask;
import com.maxiee.attitude.common.tagview.Tag;
import com.maxiee.attitude.common.tagview.TagView;
import com.maxiee.attitude.database.api.AddEventApi;
import com.maxiee.attitude.database.api.AddEventLabelRelationApi;
import com.maxiee.attitude.database.api.AddLabelsApi;
import com.maxiee.attitude.ui.dialog.NewLabelDialog;

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
    private TagView mTagViewAdded;
    private TagView mTagViewToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditEvent = (EditText) findViewById(R.id.edit_event);
        mEditFirstThought = (EditText) findViewById(R.id.first_thought);
        mTagViewAdded = (TagView) findViewById(R.id.tagview_added);
        mTagViewToAdd = (TagView) findViewById(R.id.tagview_to_add);

        initTagsToAdd();

        mTagViewToAdd.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if (tag.text.equals(getString(R.string.new_tag))) {
                    NewLabelDialog dialog = new NewLabelDialog(AddEventActivity.this);
                    dialog.setOnAddFinishedListener(new NewLabelDialog.OnAddFinishedListener() {
                        @Override
                        public void addLabel(String label) {
                            if (mLabels == null) {
                                mLabels = new ArrayList<String>();
                            }
                            mLabels.add(label);
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
                if (mLabels != null) {
                    mLabels.remove(tag.text);
                }
            }
        });

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

    public void initTagsToAdd() {
        final Tag newTag = new Tag(getString(R.string.new_tag));
        mTagViewToAdd.clear();
        mTagViewToAdd.addTag(newTag);
        if (mLabels == null) {
            return;
        }
        for (String tag: mLabels) {
            Tag useTag = new Tag(tag);
            useTag.isDeletable = true;
            mTagViewToAdd.addTag(useTag);
        }
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

            try {
                AddEventApi addEventApi = new AddEventApi(
                        AddEventActivity.this,
                        mStrEvent,
                        mStrFirstThought);
                addEventApi.exec();
                int eventKey = (int) addEventApi.getLatestKey();
                ArrayList<Integer> labelsKey = new AddLabelsApi(
                        AddEventActivity.this,
                        mLabels
                ).exec();
                for (int labelkey: labelsKey) {
                    new AddEventLabelRelationApi(
                            AddEventActivity.this,
                            eventKey,
                            labelkey
                    ).exec();
                }
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
