package com.example.smartfridgeapp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class DropboxClient {
    DbxClientV2 dbxClient;
    private static final String TAG = "DropboxClient";
    private static final String FILENAME_BEGINS_WITH = "video_";
    private static String videoFolderPath = null;

    public void authDropbox(String dropBoxAppKey, String dropBoxAppSecret)
            throws IOException, DbxException {
        DbxAppInfo dbxAppInfo = new DbxAppInfo(dropBoxAppKey, dropBoxAppSecret);
        DbxRequestConfig dbxRequestConfig = new DbxRequestConfig(
                "Smart-Fridge-App/0.0", Locale.getDefault().toString());
        DbxWebAuthNoRedirect dbxWebAuthNoRedirect = new DbxWebAuthNoRedirect(
                dbxRequestConfig, dbxAppInfo);
        String authorizeUrl = dbxWebAuthNoRedirect.start();

        /* Leaving these as a TODO note
        System.out.println("1. Authorize: Go to URL and click Allow : + authorizeUrl);
        System.out.println("2. Auth Code: Copy authorization code and input here ");
        String dropboxAuthCode ="";

        DbxAuthFinish authFinish = dbxWebAuthNoRedirect.finish(dropboxAuthCode);
 */
        //TODO DO NOT COMMIT THIS LINE EVER!! create a better way for it to imported...
        String authAccessToken = "";
        dbxClient = new DbxClientV2(dbxRequestConfig, authAccessToken);
    }

    public String getLatestVideoFileName() throws Exception {
        String latestFoundVideoFile = "";
        ListFolderResult result = null;
        if (dbxClient == null) {
            throw new Exception("Null dropboxclient");
        }
        try
        {
            result = dbxClient.files().listFolder("");

            while (true)
            {
                for (Metadata metadata : result.getEntries())
                {
                    if(metadata.getPathLower().contains(FILENAME_BEGINS_WITH));
                    {
                        if (latestFoundVideoFile == "")
                        {
                            latestFoundVideoFile = metadata.getName();
                        } else {
                            // TODO compare dates and use the latest
                        }
                    }
                }

                if (!result.getHasMore())
                {
                    break;
                }

                result = dbxClient.files().listFolderContinue(result.getCursor());
            }
        }
        catch (DbxException e)
        {
            e.printStackTrace();
        }
        return latestFoundVideoFile;
    }

    public boolean downloadFromDropbox(String fileName) throws Exception {
        if (videoFolderPath == null) {
            throw new Exception("Do not know where to save file");
        }
        FileOutputStream outputStream = new FileOutputStream(videoFolderPath + "/" + fileName);
        DbxDownloader downloader = null;
        try {
            downloader = dbxClient.files().download("/" + fileName);
        } catch(Exception ex) {
            Log.e(TAG, ex.toString());
            return false;
        } finally {
            if (downloader == null) {
                return false;
            }
            downloader.download(outputStream);
            outputStream.close();
            return true;
        }
    }

    public void setVideoFolderPath(String path) {
        videoFolderPath = path;
    }
}
