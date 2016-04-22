package com.maxiee.heartbeat.backup;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.database.tables.ImageTable;
import com.maxiee.heartbeat.database.tables.ThoughtResTable;
import com.maxiee.heartbeat.database.utils.DatabaseUtils;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Image;
import com.maxiee.heartbeat.model.ThoughtRes;
import com.maxiee.heartbeat.model.Thoughts;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by maxiee on 15-8-10.
 */
public class BackupManager {
    public static final String TAG = BackupManager.class.getSimpleName();
    private final static String DB = "heartbeat";
    private final static String BACKUP_PATH = "HeartBeat";
    private final static String BACKUP_PREFIX = "[Backup]";
    private static final int BUFFER_SIZE = 2048;

    public static String backupSD(Context context) {
        try {
            File curDb = context.getDatabasePath(DB);
            File bakFile = FileUtils.generateBackupFile(context);
            FileDES.doEncryptFile(curDb, bakFile);
            return bakFile.toString();
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
            // 使用解密
            if (!FileDES.doDecryptFile(in, out)) {
                return context.getString(R.string.restore_failed);
            }
            in.close();
            out.close();
            return context.getString(R.string.restore_ok);
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.restore_failed);
        }
    }

    public static String backupAll(Context context) {
        String dbPath = backupSD(context);
        if (dbPath == null) return null;
        try {
            File zipFile = FileUtils.generateBackupAllZip(context);
            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER_SIZE];
            File f = FileUtils.getImageDir();
            ArrayList<File> files = new ArrayList<>(Arrays.asList(f.listFiles()));
            files.add(new File(dbPath));
            for (File file : files) {
                FileInputStream fi = new FileInputStream(file);
                BufferedInputStream origin = new BufferedInputStream(fi, BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data,0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
            return zipFile.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String restoreAll(Context context, Intent data) {
        String path = data.getData().getPath();
        if (!path.contains(FileUtils.BACKUP_ZIP_PREFIX)) return context.getString(R.string.restore_failed);
        try {
            InputStream is = context.getContentResolver().openInputStream(data.getData());
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                int count;
                byte d[] = new byte[BUFFER_SIZE];
                String entryName = entry.getName();
                int entryType = FileUtils.detectFileType(entryName);
                if (entryType == FileUtils.FILE_TYPE_DB) {
                    File curDB = context.getDatabasePath(DB);
                    OutputStream out = new FileOutputStream(curDB);
                    // 使用解密
                    FileDES.doDecryptFileNotClose(zis, out);
                    out.close();
                }
                else if (entryType == FileUtils.FILE_TYPE_IMAGE) {
                    OutputStream out = new FileOutputStream(new File(FileUtils.getImageDir(), entryName));
                    BufferedOutputStream dest = new BufferedOutputStream(out, BUFFER_SIZE);
                    while ((count = zis.read(d, 0, BUFFER_SIZE)) != -1) {
                        dest.write(d, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
            zis.close();
            return context.getString(R.string.restore_ok);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getString(R.string.restore_failed);
    }

    public static boolean needTransGallery(Context context) {
        boolean ret = false;
        Cursor eventImage = DatabaseUtils.query(
                context, ImageTable.NAME,
                new String[] {ImageTable.ID, ImageTable.URI},
                ImageTable.URI + " like ?", new String[] {"%/%"});
        Log.d(TAG, String.format("事件图片有%d需要迁移.", eventImage.getCount()));
        if (eventImage.getCount() > 1) ret = true;
        eventImage.close();
        if (ret) return true;

        Cursor thoughtImage = DatabaseUtils.query(
                context, ThoughtResTable.NAME,
                new String[] {ThoughtResTable.ID, ThoughtResTable.TYPE,
                        ThoughtResTable.PATH, ThoughtResTable.THOUGHT_ID},
                ThoughtResTable.PATH + " like ?", new String[] {"%/%"});
        Log.d(TAG, String.format("感想图片有%d需要迁移.", thoughtImage.getCount()));
        if (thoughtImage.getCount() > 1) ret = true;
        thoughtImage.close();
        return ret;
    }

    public static void transGallery(Context context) {
        Cursor eventImage = DatabaseUtils.query(
                context, ImageTable.NAME,
                new String[] {ImageTable.ID, ImageTable.URI},
                ImageTable.URI + " like ?", new String[] {"%/%"});
        Log.d(TAG, String.format("事件图片有%d需要迁移.", eventImage.getCount()));
        int transCount = 0;
        if (eventImage.getCount() > 1) {
            while (eventImage.moveToNext()) {
                Image i = ImageUtils.queryImage(eventImage);
                try {
                    String filename = FileUtils.copyImageToHeartBeat(i.getPath());
                    ImageUtils.updateImageByImageId(context, i.getId(), filename);
                    transCount++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, String.format("成功迁移%d幅图片至心动目录.", transCount));
        eventImage.close();
        transCount = 0;

        Cursor thoughtImage = DatabaseUtils.query(
                context, ThoughtResTable.NAME,
                new String[] {ThoughtResTable.ID, ThoughtResTable.TYPE,
                        ThoughtResTable.PATH, ThoughtResTable.THOUGHT_ID},
                ThoughtResTable.PATH + " like ?", new String[] {"%/%"});
        Log.d(TAG, String.format("感想图片有%d需要迁移.", thoughtImage.getCount()));
        if (thoughtImage.getCount() > 1) {
            while (thoughtImage.moveToNext()) {
                ThoughtRes r = ThoughtUtils.queryThoughtRes(thoughtImage);
                if (r.getResType() != Thoughts.Thought.RES_IMAGE) continue;
                try {
                    String filename = FileUtils.copyImageToHeartBeat(r.getPath());
                    ThoughtUtils.updateRes(context, r.getThoughtId(), r.getResType(), filename);
                    transCount++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, String.format("成功迁移%d幅图片至心动目录.", transCount));
        thoughtImage.close();
    }
}
