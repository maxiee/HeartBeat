package com.maxiee.heartbeat.backup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by maxiee on 15-8-10.
 */
public class BackupManager {
    private final static String DB = "heartbeat";
    private final static String BACKUP_PATH = "HeartBeat";
    private final static String BACKUP_PREFIX = "[Backup]";
    private final static int LENGTH_OF_SQLITE_MAGIC_NUMBER = 15;
    private final static String SQLITE_MAGIC_NUMBER = "SQLite format 3";

    public static String backupSD(Context context, boolean showToast) {
        try {
            File curDB = context.getDatabasePath(DB);
            File backupPath = new File(Environment.getExternalStorageDirectory(), BACKUP_PATH);
            Log.d("maxiee", "备份目录"+backupPath.toString());
            Log.d("maxiee", "是否存在："+String.valueOf(backupPath.exists()));
            if (!backupPath.exists()) {
                backupPath.mkdir();
            }
            Log.d("maxiee", "是否创建成功："+String.valueOf(backupPath.exists()));
            File bakDB = new File(
                    backupPath,
                    BACKUP_PREFIX + "[" + DB + "]" + "[" + TimeUtils.getDate(context) + "]");
            Log.d("maxiee", "备份文件地址："+bakDB.toString());
            bakDB.createNewFile();
            FileChannel cur = new FileInputStream(curDB).getChannel();
            FileChannel bak = new FileOutputStream(bakDB).getChannel();
            bak.transferFrom(cur, 0, cur.size());
            if (showToast) {
                Toast.makeText(context, context.getString(R.string.backup_ok), Toast.LENGTH_LONG).show();
            }
            return bakDB.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public static void backupCloud(Context context) {
        String path = backupSD(context, false);
        if (path == null) {
            Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_LONG).show();
            return;
        }
        Uri db = Uri.fromFile(new File(path));
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_STREAM, db);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(i);
        } else {
            Toast.makeText(context, context.getString(R.string.share_not_found), Toast.LENGTH_LONG).show();
        }
    }

    public static void restore(Context context, Intent data) {
        if (!checkFileType(context, data.getData())) {
            Toast.makeText(context, context.getString(R.string.filetype_error), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            File curDB = context.getDatabasePath(DB);
            OutputStream out = new FileOutputStream(curDB);
            InputStream in = context.getContentResolver().openInputStream(data.getData());
            byte[] buf = new byte[1024];
            int len;
            while ((len=in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Toast.makeText(context, context.getString(R.string.restore_ok), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.restore_failed), Toast.LENGTH_LONG).show();}
    }

    private static boolean checkFileType(Context context, Uri uri) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[LENGTH_OF_SQLITE_MAGIC_NUMBER];
            in.read(buffer);
            return new String(buffer).equals(SQLITE_MAGIC_NUMBER);
        } catch (Exception e) {e.printStackTrace();}
        return false;
    }
}
