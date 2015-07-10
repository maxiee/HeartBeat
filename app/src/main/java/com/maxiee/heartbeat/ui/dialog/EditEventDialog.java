package com.maxiee.heartbeat.ui.dialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.database.api.AddEventLabelRelationApi;
import com.maxiee.heartbeat.database.api.AddLabelsApi;
import com.maxiee.heartbeat.database.api.DeleteEventLabelRelationApi;
import com.maxiee.heartbeat.database.api.GetLabelsAndFreqApi;
import com.maxiee.heartbeat.database.api.GetLabelsByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetOneLabelApi;
import com.maxiee.heartbeat.database.api.HasLabelApi;
import com.maxiee.heartbeat.database.api.UpdateEventApi;
import com.maxiee.heartbeat.model.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by maxiee on 15-7-9.
 */
public class EditEventDialog extends AppCompatDialog{
    private static final String TAG = EditEventDialog.class.getSimpleName();

    private Event mEvent;
    private String mEventText;
    private EditText mEditEvent;
    private TagView mTagViewRecent;
    private TagView mTagViewToAdd;
    private Toolbar mToolbar;
    private ArrayList<String> mLabels;
    private ArrayList<String> mLabelsBackup;
    private OnEditFinishedListener mCallback;

    public interface OnEditFinishedListener {
        void update(String event);
    }

    public EditEventDialog(Context context, Event event) {
        super(context, R.style.AppTheme_Dialog);
        mEvent = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_event);

        mEditEvent = (EditText) findViewById(R.id.edit_event);
        mTagViewRecent = (TagView) findViewById(R.id.tagview_added);
        mTagViewToAdd = (TagView) findViewById(R.id.tagview_to_add);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mToolbar.inflateMenu(R.menu.dialog_edit_thought);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.done) {
                    mEventText = mEditEvent.getText().toString();
                    if (mEventText.isEmpty()) {
                        Toast.makeText(
                                getContext(),
                                R.string.notempty,
                                Toast.LENGTH_LONG
                        ).show();
                        return false;
                    }
                    new UpdateEventTask().execute();
                    return true;
                }
                return false;
            }
        });

        mToolbar.setTitle(getContext().getString(R.string.dialog_edit_event));

        mEditEvent.setText(mEvent.getmEvent());

        mLabels = new GetLabelsByEventKeyApi(getContext(), mEvent.getmId()).exec();
        if (mLabels == null) mLabels = new ArrayList<String>();
        mLabelsBackup = new ArrayList<>(mLabels);

        initTagsToAdd();
        initTagsRecent();

        mTagViewToAdd.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                // add new label(tag)
                if (tag.text.equals(getContext().getString(R.string.new_tag))) {
                    NewLabelDialog dialog = new NewLabelDialog(getContext());
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

        // recent tags adding
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
    }

    public void initTagsToAdd() {
        final Tag newTag = new Tag(getContext().getString(R.string.new_tag));
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
                new GetLabelsAndFreqApi(getContext()).exec();
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
                    new GetOneLabelApi(getContext(), labelId.getKey()).exec()
            );
            tag.hasExtraInfo = true;
            tag.extraInfoString = " x" + String.valueOf(labelId.getValue());
            tag.layoutColor = getContext().getResources().getColor(R.color.tag_gray);
            tag.tagTextSize = 10.0f;
            mTagViewRecent.addTag(tag);
        }
    }

    public void setOnEditFinishedListener(OnEditFinishedListener callback) {mCallback = callback;}

    private class UpdateEventTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            new UpdateEventApi(getContext(), mEvent.getmId(), mEventText).exec();
            for (String l:mLabels) {
                if (new HasLabelApi(getContext(), l).exec() == HasLabelApi.NOT_FOUND) {
                    Log.d(TAG, "label to add: " + l);
                    // Todo: need an AddLabelApi
                    ArrayList<Integer> labelId = new AddLabelsApi(getContext(), l).exec();
                    Log.d(TAG, labelId.toString());
                    new AddEventLabelRelationApi(getContext(), mEvent.getmId(), labelId.get(0)).exec();
                }
            }
            for (String l:mLabelsBackup)
                if (!mLabels.contains(l)) {
                    Log.d(TAG, "label to delete: " + l);
                    int key = new HasLabelApi(getContext(), l).exec();
                    if (key != HasLabelApi.NOT_FOUND) {
                        new DeleteEventLabelRelationApi(getContext(), mEvent.getmId(), key).exec();
                    }
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismiss();
            mCallback.update(mEventText);
        }
    }
}
