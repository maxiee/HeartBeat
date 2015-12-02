package com.maxiee.heartbeat.backup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by maxiee on 15-8-10.
 */
public class BackupManager {
    private final static String DB = "heartbeat";
    private final static String BACKUP_PATH = "HeartBeat";
    private final static String BACKUP_PREFIX = "[Backup]";

    public static String backupSD(Context context) {
        try {
            File curDB = context.getDatabasePath(DB);
            File backupPath = new File(Environment.getExternalStorageDirectory(), BACKUP_PATH);
            if (!backupPath.exists()) {
                backupPath.mkdir();
            }
            File bakDB = new File(
                    backupPath,
                    BACKUP_PREFIX + "[" + DB + "]" + "[" + TimeUtils.getDate(context) + "]");
            bakDB.createNewFile();
            // 使用加密
            FileDES.doEncryptFile(curDB, bakDB);
            return bakDB.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String backupCloud(Context context) {
        String path = backupSD(context);
        if (path == null) return context.getString(R.string.backup_failed);
        Uri db = Uri.fromFile(new File(path));
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_STREAM, db);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(i);
        } else {
            return context.getString(R.string.share_not_found);
        }
        return null;
    }

    public static String restore(Context context, Intent data) {
        try {
            File curDB = context.getDatabasePath(DB);
            OutputStream out = new FileOutputStream(curDB);
            InputStream in = context.getContentResolver().openInputStream(data.getData());
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len=in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            out.close();
//            in.close();
            // 使用解密
            if (!FileDES.doDecryptFile(in, out)) {
                return context.getString(R.string.restore_failed);
            }
            return context.getString(R.string.restore_ok);
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.restore_failed);
        }
    }
}
