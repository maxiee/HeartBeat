package com.maxiee.heartbeat.backup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.maxiee.heartbeat.R;

import java.io.File;

/**
 * Created by maxiee on 15-8-10.
 */
public class BackupManager {
    private final static String DB = "heartbeat";

    public static void backup(Context context) {
        Uri db = Uri.fromFile(context.getDatabasePath(DB));
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setDataAndType(
                db,
                "*/*"
        );
        if (i.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(i);
        } else {
            Toast.makeText(context, context.getString(R.string.share_not_found), Toast.LENGTH_LONG).show();
        }
    }
}
