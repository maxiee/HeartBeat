package com.maxiee.attitude.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;


/**
 * Created by maxiee on 15-5-17.
 */
public abstract class DialogAsyncTask extends AsyncTask<Void, Integer, String>{

    private ProgressDialog progressDialog;
    private Context mContext;
    protected boolean mTaskSuccess = false;

    public DialogAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("正在提交");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
        progressDialog.dismiss();

        if (mTaskSuccess) {
            showSuccessDialog(msg);
        } else {
            showFailedDialog(msg);
        }
    }

    private void showSuccessDialog(String msg) {
        new AlertDialog.Builder(mContext)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        onFinish();
                    }
                })
                .create()
                .show();
    }

    private void showFailedDialog(String msg) {
        new AlertDialog.Builder(mContext)
                .setMessage(msg)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    public Context getmContext() {
        return mContext;
    }

    public abstract void onFinish();
}