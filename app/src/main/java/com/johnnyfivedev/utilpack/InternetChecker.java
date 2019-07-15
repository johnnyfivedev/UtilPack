package com.johnnyfivedev.utilpack;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

// https://stackoverflow.com/a/27312494/6325722
public class InternetChecker extends AsyncTask<Void, Void, Boolean> {

    private Callback callback;


    public InternetChecker(Callback consumer) {
        this.callback = consumer;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean internetAvailable) {
        if (callback != null) {
            callback.accept(internetAvailable);
        }
    }

    public interface Callback {
        void accept(Boolean internetAvailable);
    }
}