package com.example.smartfridgeapp;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.dropbox.core.DbxException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class DownloadVideoService extends IntentService {
    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;
    private static final String TAG = "SmartFridgeApp_DownloadIntentService";
    private static DropboxClient dropboxClient = new DropboxClient();
    private static String latestVideoFileName = null;

    public DownloadVideoService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent (Intent intent){
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle bundle=new Bundle();
        boolean success = false;

        //authenticate
        try {
            dropboxClient.authDropbox(getAppKey(), getAppSecretKey());
        } catch (IOException e) {
            Log.e(TAG, "IOException" + e.toString());
            e.printStackTrace();
        } catch (DbxException e) {
            Log.e(TAG, "DbxException" + e.toString());
            e.printStackTrace();
        }

        String path = getApplicationInfo().dataDir;
        dropboxClient.setVideoFolderPath(path);

        String downloadedVideoFileName = null;
        try {
            downloadedVideoFileName = dropboxClient.getLatestVideoFileName();
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve latest video file" + e.toString());
            e.printStackTrace();
        }

        try {
            if (downloadedVideoFileName == null || !dropboxClient.downloadFromDropbox(downloadedVideoFileName))
            {
                // find the latest in local
                File directory = new File(path);
                File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++ ) {
                    latestVideoFileName = dropboxClient.earlierVideoByFileName(latestVideoFileName, files[i].getName());
                }
                Log.w(TAG, "Could not download new video, using latest in local");
            } else {
                success = true;
                // TODO dropboxClient.downloadFromDropbox does same replacemnt on filename internally. There should be a way to do it for both at once (the input name is used for both downloading and saving now)
                latestVideoFileName = "/data/data/com.example.smartfridgeapp/" + downloadedVideoFileName.replace(".", "_") + ".mp4";
            }
        } catch (DbxException e) {
            Log.e(TAG, "DbxException" + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException" + e.toString());
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e(TAG, "Exception" + e.toString());
            e.printStackTrace();
        }

        if (success) {
            receiver.send(SUCCESS_CODE, bundle);
        } else {
            receiver.send(ERROR_CODE, bundle);
        }
    }

    public static String getLatestVideoFileName() {
        //TODO is null after download
        return latestVideoFileName;
    }

    @NotNull
    @Contract(pure = true)
    private String getAppSecretKey()
    {
        //TODO
        return "";
    }

    @NotNull
    @Contract(pure = true)
    private String getAppKey()
    {
        //TODO
        return "";
    }
}