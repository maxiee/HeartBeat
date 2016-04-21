package com.maxiee.heartbeat.backup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.maxiee.heartbeat.R;

/**
 * Created by maxiee on 16/4/21.
 */
public class BackupAllTask extends AsyncTask<Void, Void, String> {
    private Context mContext;
    ProgressDialog mProgressDialog;

    public BackupAllTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(R.string.backuping));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return BackupManager.backupAll(mContext);
    }

    @Override
    protected void onPostExecute(String ret) {
        mProgressDialog.cancel();
        if (ret != null) {
            Toast.makeText(
                    mContext, mContext.getString(R.string.backup_ok),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(
                    mContext, mContext.getString(R.string.backup_failed),
                    Toast.LENGTH_LONG).show();
        }
    }
}
