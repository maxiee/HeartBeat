package com.maxiee.heartbeat.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.database.api.AddThoughtApi;

/**
 * Created by maxiee on 15-6-14.
 */
public class NewThoughtDialog extends AppCompatDialog {

    private int mEventKey;
    private Toolbar mToolbar;
    private EditText mEditThought;
    private String mTextThought;
    private OnAddFinishedListener mCallback;

    public interface OnAddFinishedListener {
        public void update();
    }

    public NewThoughtDialog(Context context, int eventKey) {
        super(context, R.style.AppTheme_Dialog);
        mEventKey = eventKey;
    }

    public void setOnAddFinishedListener(OnAddFinishedListener callback) {
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_thought);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditThought = (EditText) findViewById(R.id.edit_thought);

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mToolbar.inflateMenu(R.menu.dialog_new_thought);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add) {
                    mTextThought = mEditThought.getText().toString();
                    if (mTextThought.isEmpty()) {
                        Toast.makeText(
                                getContext(),
                                R.string.notempty,
                                Toast.LENGTH_LONG).show();
                        return true;
                    }

                    new AddThoughtTask().execute();
                    return true;
                }
                return false;
            }
        });

        String title = getContext().getString(R.string.add_thought);
        mToolbar.setTitle(title);
    }

    private class AddThoughtTask extends AsyncTask<Void,Void,Void> {

        private ProgressDialog mProgress;
        private boolean mAddSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress = new ProgressDialog(getContext());
            mProgress.setCancelable(false);
            String message = getContext().getString(R.string.adding);
            mProgress.setMessage(message);
            mProgress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            new AddThoughtApi(
                    getContext(),
                    mEventKey,
                    mTextThought).exec();
            mAddSuccess = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mAddSuccess) {
                mCallback.update();
            }
            mProgress.dismiss();
            dismiss();
        }
    }
}
