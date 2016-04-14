package com.maxiee.heartbeat.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import com.maxiee.heartbeat.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by maxiee on 16/3/25.
 */
public class GalleryUtils {
    private static final int ADD_IMAGE = 1127;

    private static boolean useInternalGallery(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_internal_gallery", false);
    }


    public static void openGallery(Context context) {
        if (useInternalGallery(context)) {
            openInternalGallery(context);
        } else {
            openSystemGallery(context);
        }
    }

    private static void openSystemGallery(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            ((Activity) context).startActivityForResult(
                    Intent.createChooser(i, context.getString(R.string.add_image)),
                    ADD_IMAGE);
        } else {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            ((Activity) context).startActivityForResult(
                    Intent.createChooser(i, context.getString(R.string.add_image)),
                    ADD_IMAGE);
        }
    }

    private static void openInternalGallery(Context context) {
        Intent i = new Intent(context, MultiImageSelectorActivity.class);
        i.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        i.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        ((Activity) context).startActivityForResult(i, ADD_IMAGE);
    }

    public static String onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            String path;
            if (useInternalGallery(context)) {
                path = onInternalResult(context, data);
            } else {
                path = onSystemResult(context, data);
            }
            File fileToMove = new File(path);
            String fileName = fileToMove.getName();
            File fileMoved = new File(FileUtils.getImageDir(), fileName);
            try {
                FileChannel toMove = new FileInputStream(fileToMove).getChannel();
                FileChannel moved = new FileOutputStream(fileMoved).getChannel();
                moved.transferFrom(toMove, 0, toMove.size());
                path = fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }
        return null;
    }

    private static String onSystemResult(Context context, Intent data) {
        Uri mImageUri = data.getData();
        if (Build.VERSION.SDK_INT >= 19) {
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //noinspection ResourceType
            context.getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
        }
        // convert uri to path
        return FileUtils.uriToPath(context, mImageUri);
    }

    private static String onInternalResult(Context context, Intent data) {
        List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
        return path.get(0);
    }

    public static String getImagePath(String oldPath) {
        // old format
        if (oldPath.contains("/")) {
            return oldPath;
        }
        // new format
        return new File(FileUtils.getImageDir(), oldPath).toString();
    }
}
