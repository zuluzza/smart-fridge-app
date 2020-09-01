package com.example.smartfridgeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SmartFridgeApp_MainActivity";
    private static final String appFolderPathInDropbox = "smart-fridge";
    private static String currentVideoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate begin");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PendingIntent pendingResult = createPendingResult(
                DownloadVideoService.DOWNLOAD_VIDEO_REQUEST_CODE, new Intent(), 0);
        Intent intent = new Intent(getApplicationContext(), DownloadVideoService.class);
        startService(intent);
        //TODO show the last

        Log.d(TAG, "onCreate end");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DownloadVideoService.DOWNLOAD_VIDEO_REQUEST_CODE) {
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

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startVideoPlayback(View view) {
        setContentView(R.layout.activity_main);

        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        //MediaController mediaController = new MediaController(this);
        // mediaController.setAnchorView(videoView);
        //videoView.setMediaController(mediaController);
        Log.d(TAG, "Going to show video from " + currentVideoFile);

        videoView.setVideoPath(currentVideoFile);

        videoView.start();
    }
}
