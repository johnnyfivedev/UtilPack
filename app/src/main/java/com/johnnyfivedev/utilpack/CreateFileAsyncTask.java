package com.johnnyfivedev.utilpack;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;

public class CreateFileAsyncTask extends AsyncTask<Uri, Void, File> {

    private final FileManager fileManager;
    private final Callback callback;


    public CreateFileAsyncTask(FileManager fileManager, Callback callback) {
        this.fileManager = fileManager;
        this.callback = callback;
    }

    @Override
    protected File doInBackground(Uri... uris) {
        return fileManager.createTempFileInCacheDir(uris[0]);
    }

    @Override
    protected void onPostExecute(File result) {
        if (callback != null) {
            callback.onResult(result);
        }
    }

    public interface Callback {
        void onResult(File file);
    }
}
