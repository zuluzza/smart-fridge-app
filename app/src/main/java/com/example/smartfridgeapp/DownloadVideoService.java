package com.example.smartfridgeapp;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.dropbox.core.DbxException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DownloadVideoService extends IntentService {
    public static final int DOWNLOAD_VIDEO_REQUEST_CODE = 200;
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

        String latestVideoFile = null;
        try {
            latestVideoFile = dropboxClient.getLatestVideoFileName();
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve latest video file" + e.toString());
            e.printStackTrace();
        }

        try {
            if (latestVideoFile == null) {
                Log.e(TAG, "Cannot work without a video file");
            }
            else if (!dropboxClient.downloadFromDropbox(latestVideoFile))
            {
                //TODO use latest in local
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
    }

    public static String getLatestVideoFileName() {
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