package com.example.smartfridgeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.dropbox.core.DbxException;
import com.example.smartfridgeapp.DropboxClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static DropboxClient dropboxClient = new DropboxClient();

    String getAppSecretKey()
    {
        //TODO
        return "1234";
    }

    String getAppKey()
    {
        //TODO
        return "asdf";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //authenticate
        try {
            dropboxClient.authDropbox(getAppKey(), getAppSecretKey());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        //TODO get latest file name
        String latestVideoFile = "path_to_it";
        //TODO download videos
        try {
            if (!dropboxClient.downloadFromDropbox(latestVideoFile))
            {
                //TODO use latest in local
            }
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO show the last
    }
}
