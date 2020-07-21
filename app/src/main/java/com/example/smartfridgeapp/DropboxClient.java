package com.example.smartfridgeapp;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.v1.DbxEntry; //why there's only v1 entry? :O
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
//TODO take V2 to use. V1 is used because the tutorial this follows is apparently made for it
import com.dropbox.core.v1.DbxClientV1;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class DropboxClient {
    DbxClientV1 dbxClient;

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
        */

        String dropboxAuthCode = new BufferedReader(new InputStreamReader(
                System.in)).readLine().trim();
        DbxAuthFinish authFinish = dbxWebAuthNoRedirect.finish(dropboxAuthCode);
        String authAccessToken = authFinish.getAccessToken();
        dbxClient = new DbxClientV1(dbxRequestConfig, authAccessToken);
    }

    public DbxEntry.WithChildren listDropboxFolders(String folderPath) throws DbxException {
        return dbxClient.getMetadataWithChildren(folderPath);
    }

    public boolean downloadFromDropbox(String fileName) throws DbxException,
            IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        try {
            DbxEntry.File downloadedFile = dbxClient.getFile("/" + fileName,
                    null, outputStream);
        } catch(Exception ex) {
            //TODO log exception
            return false;
        } finally {
            outputStream.close();
            return true;
        }
    }
}
