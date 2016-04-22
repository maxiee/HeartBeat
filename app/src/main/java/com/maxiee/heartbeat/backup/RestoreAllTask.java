package com.maxiee.heartbeat.backup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.maxiee.heartbeat.R;

/**
 * Created by maxiee on 16/4/21.
 */
public class RestoreAllTask extends AsyncTask<Intent, Void, String>{
    private Context mContext;
    ProgressDialog mProgressDialog;

    public RestoreAllTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.restoring));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(Intent... params) {
        return BackupManager.restoreAll(mContext, params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        mProgressDialog.cancel();
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
}
