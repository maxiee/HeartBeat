package com.maxiee.heartbeat.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by maxiee on 15-9-5.
 */
public class FileUtils {
    private final static String HB_PATH = "HeartBeat";
    public static final String LONG_IMAGE_PATH = "LongImage";

    private final static String DB = "heartbeat";
    private final static String BACKUP_PATH = "Backup";
    private final static String BACKUP_PREFIX = "[Backup]";
    public static final String IMAGE_PATH = "Image";


    public static String saveLongImage(Context context, Bitmap bitmap) {
        try {

            File heartBeatPath = new File(Environment.getExternalStorageDirectory(), HB_PATH);
            if (!heartBeatPath.exists()) {
                heartBeatPath.mkdir();
            }
            File longImagePath = new File(heartBeatPath, LONG_IMAGE_PATH);
            if (!longImagePath.exists()) {
                longImagePath.mkdir();
            }
            File longImageFile = new File(
                    longImagePath,
                    "[" + LONG_IMAGE_PATH + "][" + TimeUtils.getDate(context) + "].png"
            );
            longImageFile.createNewFile();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
            byte[] bytes = os.toByteArray();

            FileOutputStream fos = new FileOutputStream(longImageFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return longImageFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("NewApi")
    public static String uriToPath(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            Log.d("maxiee", "docID:" + docId);
            final String[] split = docId.split(":");
            final String type = split[0];
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {split[1]};
            return getDataColumn(context, contentUri, selection, selectionArgs);
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private static File createDirIfNotExist(File dir) {
        if (!dir.exists()) dir.mkdir();
        return dir;
    }

    public static File getHeartbeatDir() {
        File rootDir = new File(Environment.getExternalStorageDirectory(), HB_PATH);
        return createDirIfNotExist(rootDir);
    }

    public static File getBackupDir() {
        File rootDir = getHeartbeatDir();
        File bakDir = new File(rootDir, BACKUP_PATH);
        return createDirIfNotExist(bakDir);
    }

    public static File getImageDir() {
        File rootDir = getHeartbeatDir();
        File imageDir = new File(rootDir, IMAGE_PATH);
        return createDirIfNotExist(imageDir);
    }

    public static File generateBackupFile(Context context) throws IOException {
        File bakDir = getBackupDir();
        File bakFile = new File(
                bakDir,
                BACKUP_PREFIX + String.format("[%s][%s]", DB, TimeUtils.getDate(context)));
        bakFile.createNewFile();
        return bakFile;
    }

    public static String copyImageToHeartBeat(String path) throws IOException {
        File fileToMove = new File(path);
        String fileName = fileToMove.getName();
        File fileMoved = new File(FileUtils.getImageDir(), fileName);
        FileChannel toMove = new FileInputStream(fileToMove).getChannel();
        FileChannel moved = new FileOutputStream(fileMoved).getChannel();
        moved.transferFrom(toMove, 0, toMove.size());
        toMove.close();
        moved.close();
        return fileName;
    }
}
