package com.example.smartfridgeapp;

import android.util.Log;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        //TODO DO NOT COMMIT THIS LINE EVER!! create a better way for it to imported...
        String authAccessToken = "";
        dbxClient = new DbxClientV2(dbxRequestConfig, authAccessToken);
    }
    //TODO extract somewhere else as this is now used by other class
    public String earlierVideoByFileName(String filename1, String filename2) throws ParseException {
        if (filename1 == null && filename2 == null) {
            return null;
        }
        if (filename1 == null) {
            return filename2;
        }
        if (filename2 == null) {
            return filename1;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
        Date date1 = formatter.parse(filename1.replace(FILENAME_BEGINS_WITH,""));
        Date date2 = formatter.parse(filename2.replace(FILENAME_BEGINS_WITH,""));
        return date1.after(date2) ? filename1 : filename2;
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
                        latestFoundVideoFile = (latestFoundVideoFile == "") ? metadata.getName() : earlierVideoByFileName(latestFoundVideoFile, metadata.getName());
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
        FileOutputStream outputStream = new FileOutputStream(videoFolderPath + "/" + fileName.replace(".", "_") + ".mp4");
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
