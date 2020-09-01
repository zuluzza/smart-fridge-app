package com.example.smartfridgeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SmartFridgeApp_MainActivity";
    private static String currentVideoFile = null;
    private ServiceResultReceiver downloadServiceResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate begin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadServiceResultReceiver = new ServiceResultReceiver(new Handler());
        Intent intent = new Intent(getApplicationContext(), DownloadVideoService.class);
        intent.putExtra("receiver", downloadServiceResultReceiver);
        startService(intent);

        Log.d(TAG, "onCreate end");
    }

    public void startVideoPlayback(View view) {
        if (currentVideoFile == null) {
            //TODO warn user as well?
            Log.d(TAG, "Unable to start video because file is not set");
            return;
        }
        setContentView(R.layout.activity_main);

        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        Log.d(TAG, "Going to show video from " + currentVideoFile);
        Uri fileUri = Uri.fromFile(new File(currentVideoFile));
        videoView.setVideoURI(fileUri);
        //videoView.setVideoPath(currentVideoFile);
        videoView.start();
    }

    class ServiceResultReceiver extends ResultReceiver {
        public ServiceResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
            /* POSSIBLE TODOS
            case DownloadVideoService.AUTHENTICATION_FAILED_CODE:
                break;
            case DownloadVideoService.DOWNLOAD_FAILED_CODE:
                break;
             */
            case DownloadVideoService.ERROR_CODE:
                Log.e(TAG, "Download failed!");
                break;
            case DownloadVideoService.SUCCESS_CODE:
                Log.d(TAG, "succesfully downloaded latest video");
                currentVideoFile = DownloadVideoService.getLatestVideoFileName();

                if (currentVideoFile != null) {
                    TextView tv = (TextView)findViewById(R.id.textView);
                    tv.setText("Current video " + currentVideoFile);
                }
                break;
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }
}
