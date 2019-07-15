package com.johnnyfivedev.utilpack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.List;

public abstract class IntentUtils {

    public static final int DEFAULT_REQUEST_CODE_FILE_CHOOSER = 1004;

    @Deprecated
    public static void openUri(Context context,
                               String url,
                               String mimeType) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), mimeType);
            context.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(context, "Нет подходящего приложения для открытия этого файла", Toast.LENGTH_LONG).show();
            exception.printStackTrace();
        }
    }

    public static void viewFile(Context context,
                                File file,
                                String mimeType) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mimeType);
            context.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(context, "Нет подходящего приложения для открытия этого файла", Toast.LENGTH_LONG).show();
            exception.printStackTrace();
        }
    }

    public static void openSettings(Activity activity) {
        activity.startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
    }


    public static void openApplicationSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openGallery(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openFileChooser(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select file"), requestCode);
    }

    public static void openFileChooser(Activity activity) {
        openFileChooser(activity, DEFAULT_REQUEST_CODE_FILE_CHOOSER);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void openFileChooser(Activity activity, int requestCode, List<String> mimeTypes) {
        Intent intent = new Intent();
        intent.setType("*/*");
        if (CollectionsUtils.neitherNullNorEmpty(mimeTypes)) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toArray());
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select file"), requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void openFileChooser(Activity activity, List<String> mimeTypes) {
        openFileChooser(activity, DEFAULT_REQUEST_CODE_FILE_CHOOSER, mimeTypes);
    }

    // If you want to receive result in fragment, startActivityForResult from fragment
    public static void openFileChooser(Fragment fragment, int requestCode) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select file"), requestCode);
    }

    public static void openFileChooser(Fragment fragment) {
        openFileChooser(fragment, DEFAULT_REQUEST_CODE_FILE_CHOOSER);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void openFileChooser(Fragment fragment, int requestCode, List<String> mimeTypes) {
        Intent intent = new Intent();
        intent.setType("*/*");
        if (CollectionsUtils.neitherNullNorEmpty(mimeTypes)) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toArray());
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select file"), requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void openFileChooser(Fragment fragment, List<String> mimeTypes) {
        openFileChooser(fragment, DEFAULT_REQUEST_CODE_FILE_CHOOSER, mimeTypes);
    }
}
