package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.DialogAsyncTask;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.database.api.AddEventApi;
import com.maxiee.heartbeat.database.api.AddEventLabelRelationApi;
import com.maxiee.heartbeat.database.api.AddImageApi;
import com.maxiee.heartbeat.database.api.AddLabelsApi;
import com.maxiee.heartbeat.database.api.AddThoughtApi;
import com.maxiee.heartbeat.database.api.GetLabelsAndFreqApi;
import com.maxiee.heartbeat.database.api.GetOneLabelApi;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.dialog.NewLabelDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by maxiee on 15-6-11.
 */
public class AddEventActivity extends AppCompatActivity{

    private final static String TAG = AddEventActivity.class.getSimpleName();

    private static final int ADD_IMAGE = 1127;

    public final static int ADD_EVENT_REQUEST = 100;
    public final static int ADD_EVENT_RESULT_OK = 101;

    private EditText mEditEvent;
    private EditText mEditFirstThought;
    private String mStrEvent;
    private String mStrFirstThought;
    private ArrayList<String> mLabels;
    private TagView mTagViewRecent;
    private TagView mTagViewToAdd;
    private TextView mTvAddImage;
    private ImageView mImageBackDrop;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditEvent = (EditText) findViewById(R.id.edit_event);
        mEditFirstThought = (EditText) findViewById(R.id.first_thought);
        mTagViewRecent = (TagView) findViewById(R.id.tagview_added);
        mTagViewToAdd = (TagView) findViewById(R.id.tagview_to_add);
        mTvAddImage = (TextView) findViewById(R.id.add_imgae);
        mImageBackDrop = (ImageView) findViewById(R.id.backdrop);

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

        if (mLabels == null) {
            mLabels = new ArrayList<String>();
        }

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
                if (mLabels != null) {
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
        }
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

            // add event
            int eventKey = (int) new AddEventApi(
                    AddEventActivity.this,
                    mStrEvent).exec();

            // add thought
            new AddThoughtApi(
                    AddEventActivity.this,
                    eventKey,
                    mStrFirstThought,
                    Thoughts.Thought.HAS_NO_RES,
                    Thoughts.Thought.HAS_NO_PATH
            ).exec();

            // add labels
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

            if (mImageUri != null) {
                // convert uri to path
                String path = FileUtils.uriToPath(AddEventActivity.this, mImageUri);
                new AddImageApi(AddEventActivity.this, eventKey, path).exec();
            }

            Log.d(TAG, "添加事件");
            Log.d(TAG, "id: " + String.valueOf(eventKey));
            Log.d(TAG, "labels: " + mLabels.toString());
            Log.d(TAG, "labels_key: " + labelsKey.toString());
            mTaskSuccess = true;
            return getmContext().getString(R.string.add_ok);
        }
    }

}
