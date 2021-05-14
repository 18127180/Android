package com.example.gallery_noob;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class Fullscreen_video extends AppCompatActivity {
    VideoView videoView;
    String url;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);
        getSupportActionBar().hide();

        Intent i = getIntent();
        url = i.getStringExtra("url");

        videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(url));
//        videoView.requestFocus();
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaController == null) {
                    mediaController = new MediaController(getApplicationContext());
                    videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(videoView);
                }
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        if (mediaController == null) {
                            mediaController = new MediaController(getApplicationContext());
                            videoView.setMediaController(mediaController);
                            mediaController.setAnchorView(videoView);
                        }
                    }
                });
            }
        });
        videoView.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.pause();
        videoView.seekTo(0);
    }
}