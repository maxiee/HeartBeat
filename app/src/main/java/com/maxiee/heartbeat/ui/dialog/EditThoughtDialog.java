package com.maxiee.heartbeat.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.DialogAsyncTask;
import com.maxiee.heartbeat.database.api.DeleteThoughtByKeyApi;
import com.maxiee.heartbeat.database.api.UpdateThoughtApi;

/**
 * Created by maxiee on 15-7-8.
 */
public class EditThoughtDialog extends AppCompatDialog{
    private int mThoughtKey;
    private String mThought;
    private Toolbar mToolbar;
    private EditText mEditThought;
    private OnAddFinishedListener mCallback;

    public interface OnAddFinishedListener {
        void update(String newThought);
        void remove();
    }

    public EditThoughtDialog(Context context, int thoughtKey, String thought) {
        super(context, R.style.AppTheme_Dialog);
        mThoughtKey = thoughtKey;
        mThought = thought;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_thought);

        mEditThought = (EditText) findViewById(R.id.edit_thought);
        mEditThought.setText(mThought);

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
                    mThought = mEditThought.getText().toString();
                    if (mThought.isEmpty()) {
                        Toast.makeText(
                                getContext(),
                                R.string.notempty,
                                Toast.LENGTH_LONG
                        ).show();
                        return false;
                    }
                    new UpdateThoughtTask(getContext()).execute();
                }
                if (item.getItemId() == R.id.delete) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog);
                    builder.setTitle(getContext().getString(R.string.delete));
                    builder.setMessage(getContext().getString(R.string.delete_text));
                    builder.setPositiveButton(
                            getContext().getString(R.string.ok),
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DeleteThoughtByKeyApi(getContext(), mThoughtKey).exec();
                                    mCallback.remove();
                                    dismiss();
                                    EditThoughtDialog.this.dismiss();
                                }
                            }
                    );
                    builder.setNegativeButton(
                            getContext().getString(R.string.cancel),
                            null
                    );
                    builder.show();
                }
                return false;
            }
        });

        mToolbar.setTitle(getContext().getString(R.string.dialog_edit_thought));
    }

    public void setOnAddFinishedListener(OnAddFinishedListener callback) {
        mCallback = callback;
    }

    private class UpdateThoughtTask extends DialogAsyncTask {

        public UpdateThoughtTask(Context context) {
            super(context);
        }

        @Override
        public void onFinish() {
            mCallback.update(mThought);
            dismiss();
        }

        @Override
        protected String doInBackground(Void... params) {
            new UpdateThoughtApi(
                    getmContext(),
                    mThoughtKey,
                    mThought
            ).exec();
            mTaskSuccess = true;
            return getmContext().getString(R.string.add_ok);
        }
    }
}
