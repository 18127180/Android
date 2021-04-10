package com.example.gallery_noob;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class videoFragment extends Fragment implements FragmentLifecycle {
    private VideoView videoView;
    private String url;
    private View view;
    MediaController mediaController;

    public videoFragment(String url){
        this.url=url;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.full_screen_video, container,false);
        videoView = (VideoView) view.findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        if (mediaController == null) {
                            mediaController = new MediaController(view.getContext());
                            videoView.setMediaController(mediaController);
                            mediaController.setAnchorView(videoView);
                        }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("pause", "ok");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)
            {
                videoView.pause();
                videoView.seekTo(0);
                mediaController=null;
                videoView.setMediaController(mediaController);
            }
        }
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
        videoView.start();
        if (mediaController == null) {
            mediaController = new MediaController(view.getContext());
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
        }
        if(FullScreenImage.favList != null){
            if(FullScreenImage.favList.contains(url)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    FullScreenImage.fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite_24,0,0);
                    FullScreenImage.favStatus = true;
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    FullScreenImage.fav.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_favorite,0,0);
                    FullScreenImage.favStatus = false;
                }
            }
        }
    }
}
